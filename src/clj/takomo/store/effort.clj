(ns takomo.store.effort
  (:require [datahike.api :as d]
            [takomo.store :refer [get-db get-conn]]))

(def effort-keys [:effort/startDate :effort/reference :effort/endDate :effort/description :effort/task :effort/assignee])

(defn create-effort [effort]
  (d/transact! (get-conn) [(select-keys effort effort-keys)]))


(defn postprocess [effort]
  (-> effort
      (update :effort/assignee #(get % :db/id))
      (update :effort/task #(get % :db/id))))

(defn read-efforts []
  (->> (get-db)
       (d/q '[:find [(pull ?e [*]) ...] :where [?e :effort/reference ?r]])
       (mapv postprocess)))

(defn read-effort-by-id [id]
  (-> (get-db)
      (d/pull '[*] id)
      postprocess))

(defn read-effort-by-reference [reference]
  (read-effort-by-id [:effort/reference reference]))

(defn update-effort [{:keys [:db/id] :as effort}]
  (if-not id
    (throw (ex-info "id should not be nil" effort))
    (d/transact! (get-conn) [(select-keys effort (conj effort-keys :db/id))])))

(defn delete-effort [id]
  (d/transact! (get-conn) [[:db/retractEntity id]]))
