(ns takomo.editor
  (:require [cljfx.api :as fx]
            [clojure.core.cache :as cache]
            [datahike.api :as d])
  (:import [javafx.scene.input MouseEvent KeyEvent KeyCode]))


;; create and populate database
(def uri "datahike:mem:///editor")

(def schema [{:db/ident :user/name
              :db/valueType :db.type/string
              :db/cardinality :db.cardinality/one}
             {:db/ident :user/age
              :db/valueType :db.type/long
              :db/cardinality :db.cardinality/one}])

(comment
  (d/delete-database uri)

  (d/create-database uri :initial-tx schema)

  )

(def conn (d/connect uri))

(d/transact conn (->> (range 20)
                       (mapv #(hash-map
                               :db/id (str %)
                               :user/name (str "user" %)
                               :user/age (+ 13 (rand-int 13))))))

;; we have fx context as state, and put db there. this value might eventually take a lot
;; of memory, so make sure to use cache

(def *context
  (atom (fx/create-context
          {:db (d/db conn)
           :18+ false
           :query-result #{}
           :typed-query ""}
          cache/lru-cache-factory)))

;; we listen to db changes and update db in context when detect new txs
;; in case of datomic you might use `tx-report-queue` for this

(datahike.core/listen! conn :ui (fn [_] (swap! *context fx/swap-context assoc :db (d/db conn))))

;; subscriptions

(defn- query-sub [ctx q & inputs]
  (apply d/q q (fx/sub ctx :db) inputs))

(defn- all-user-ids-sub [ctx]
  (fx/sub ctx query-sub '[:find [?e ...]
                          :where [?e :user/name]]))

(defn- grown-up-user-ids-sub [ctx]
  (fx/sub ctx query-sub '[:find [?e ...]
                          :where
                          [?e :user/name]
                          [?e :user/age ?a]
                          [(<= 18 ?a)]]))

(defn- value-sub [ctx id attr]
  (fx/sub ctx query-sub '[:find ?v . :in $ ?e ?a :where [?e ?a ?v]] id attr))

;; views

;; javafx's columns have :editable property which does not really play well with cljfx,
;; so it's easier to completely disregard provided editable functionality and reimplement
;; it yourself, making it data driven. To do this, we have :edit key in app state context,
;; which has entity id and attribute when it's edited, and we use this key in cell-factory
;; to render editable or normal version of a cell.

(defn- editable-cell [{:keys [fx/context id attr value-converter]}]
  (let [edit (fx/sub context :edit)
        value (fx/sub context value-sub id attr)]
    (if (= edit [id attr])
      {:fx/type :text-field
       :on-key-pressed {:event/type ::key-pressed}
       :text-formatter {:fx/type :text-formatter
                        :value-converter value-converter
                        :value value
                        :on-value-changed {:event/type ::edit
                                           :attr attr
                                           :id id}}}
      {:fx/type :label
       :on-mouse-clicked {:event/type ::on-cell-click :id id :attr attr}
       :text (str value)})))

(defn- make-attr-cell-factory [view attr value-converter]
  ;; cell factory receives item and has to return prop map without :fx/type to satisfy
  ;; javafx cell renderer which has to establish type of cell beforehand. We still want to
  ;; access context, so we have to use :graphic property which can be a normal component
  (fn [id]
    {:text ""
     :graphic {:fx/type view
               :id id
               :attr attr
               :value-converter value-converter}}))

(def name-cell-factory (make-attr-cell-factory editable-cell :user/name :default))

(def age-cell-factory (make-attr-cell-factory editable-cell :user/age :long))

(defn- result-table [{:keys [fx/context]}]
  (let [result-set (fx/sub context :query-result)
        query (fx/sub context :typed-query)]
    {:fx/type :table-view
     :editable false
     :columns (if (= query "")
                []
                (->> query
                     read-string
                     datalog.parser/parse
                     :qfind
                     :elements
                     (map :symbol)
                     (map-indexed
                      (fn [idx itm]
                        {:fx/type :table-column
                         :text (str itm)
                         :cell-value-factory (fn [e] (get e idx))}))
                     vec))
     :items result-set}))


(defn note-input [{:keys [fx/context]}]
  {:fx/type :text-area
   :style-class "input"
   :text  (fx/sub context :typed-query)
   :on-text-changed {:event/type ::type-query}})

(defn note-button [{:keys [fx/context]}]
  {:fx/type :button
   :text "Run query"
   :style-class "foo"
   :on-action {:event/type ::run-query
               :query (fx/sub context :typed-query)}})

(defn- root-view [{:keys [fx/context]}]
  (let [grown-ups (fx/sub context :18+)]
    {:fx/type :stage
     :showing true
     :scene {:fx/type :scene
             :stylesheets #{"markdown.css"}
             :root {:fx/type :v-box
                    :spacing 10
                    :padding 20
                    :children [{:fx/type note-input :grid-pane/column 0}
                               {:fx/type note-button }
                               {:fx/type result-table}
                               #_{:fx/type user-table
                                :user-ids-query (if grown-ups
                                                  grown-up-user-ids-sub
                                                  all-user-ids-sub)}]}}}))

;; events

(defmulti event-handler :event/type)

(defmethod event-handler :default [e]
  (prn (:event/type e) (:fx/event e) (dissoc e :fx/context :fx/event :event/type)))

(defmethod event-handler ::on-cell-click [{:keys [fx/context ^MouseEvent fx/event attr id]}]
  (when (= 2 (.getClickCount event))
    {:context (fx/swap-context context assoc :edit [id attr])}))

(defmethod event-handler ::edit [{:keys [fx/context fx/event attr id]}]
  {:context (fx/swap-context context dissoc :edit)
   :transact [[:db/add id attr event]]})

(defmethod event-handler ::key-pressed [{:keys [fx/context ^KeyEvent fx/event]}]
  (when (= (.getCode event) KeyCode/ESCAPE)
    {:context (fx/swap-context context dissoc :edit)}))

(defmethod event-handler ::set-18+ [{:keys [fx/context fx/event]}]
  {:context (fx/swap-context context assoc :18+ event)})

(defmethod event-handler ::type-query [{:keys [fx/event fx/context]}]
  {:context (fx/swap-context context assoc :typed-query event)})

(defmethod event-handler ::run-query [{:keys [fx/context query]}]
  {:context (fx/swap-context context assoc :query-result (if query (d/q (read-string query) @conn) "NIL"))})

;; app

(comment

  (def app
    (fx/create-app *context
                   :event-handler event-handler
                   :effects {:transact (fn [tx-data _]
                                         (d/transact! conn tx-data))}
                   :desc-fn (fn [_] (hash-map :fx/type root-view))))

  )
