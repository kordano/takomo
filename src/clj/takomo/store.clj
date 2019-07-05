(ns takomo.store
  (:require [datahike.api :as d]
            [hasch.core :as h]))

(defonce state (atom {:uri "datahike:mem://takomo"
                     :conn nil}))

(defn init []
  (let [uri (:uri @state)
        schema-tx (-> "resources/dh/schema.edn" slurp read-string)]
    (d/create-database uri schema-tx)
    (swap! state assoc :conn (d/connect uri))))


(defn create-member [{:keys [firstname lastname email password]}]
  (d/transact! (:conn @state) [{:member/firstname firstname
                                :member/lastname lastname
                                :member/email email
                                :member/salt "123"
                                :member/passhash (h/b64-hash password)}]))

(defn get-members []
  (d/q '[:find [(pull ?e [:db/id :member/firstname :member/lastname :member/email]) ...] :where [?e :member/email ?email]]
       (d/db (:conn @state))))

(defn update-member [{:keys [id firstname lastname email password] :as new-member}]
  (if-not id
    (throw (ex-info "id not found" {:id id}))
    (d/transact! (:conn @state) [(merge
                                  {:db/id id}
                                  (when firstname {:member/firstname firstname})
                                  (when lastname {:member/lastname lastname})
                                  (when email {:member/email email})
                                  (when password {:member/passhash (h/b64-hash password)}))])))

(defn get-member-by-id [id]
  (d/pull (d/db (:conn @state)) '[:db/id :member/firstname :member/lastname :member/email] id))

(defn get-member-by-email [email]
  (get-member-by-id [:member/email email]))

(defn delete-member [id]
  (d/transact! (:conn @state) [[:db/retractEntity id]]))
