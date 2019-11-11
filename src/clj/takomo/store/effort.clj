(ns takomo.store.effort
  (:require [datahike.api :as d]
            [takomo.store :refer [conn]]
            [takomo.utils :as tu]))

(def effort-keys [:effort/startDate :effort/endDate  :effort/task :effort/assignee])

(defn pre-process [{:keys [endDate] :as effort}]
  (let [new-effort
        (-> effort
            (tu/add-namespace :effort)
            (update :effort/startDate #(if % (tu/str->Date %) (java.util.Date.)))
            (select-keys effort-keys))]
    (if endDate
      (update new-effort :effort/endDate tu/str->Date)
      new-effort)))

(defn post-process [effort]
  (-> effort
      (update :effort/assignee #(get % :db/id))
      (update :effort/task #(get % :db/id))
      (update :effort/startDate tu/format-to-iso-8601-date)
      (update :effort/endDate tu/format-to-iso-8601-date)
      tu/remove-namespace))

(defn create-effort [effort]
  {:id (-> (d/transact conn [(pre-process effort)]) :tx-data last :e)})

(defn read-efforts []
  (->> @conn
       (d/q '[:find [(pull ?e [*]) ...] :where [?e :effort/assignee _]])
       (mapv post-process)))

(defn read-effort-by-id [id]
  (-> @conn
      (d/pull '[*] id)
      post-process))

(defn update-effort [{:keys [id endDate] :as effort}]
  (if-not id
    (throw (ex-info "id should not be nil" effort))
    (let [tx-data (-> (pre-process effort)
                      (assoc :db/id id))]
      (d/transact conn [tx-data]))))

(defn delete-effort [id]
  (d/transact conn [[:db/retractEntity id]]))
