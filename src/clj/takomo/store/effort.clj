(ns takomo.store.effort
  (:require [datahike.api :as d]
            [takomo.store :refer [conn]]
            [takomo.utils :as tu]))

(def effort-keys [:effort/startDate :effort/reference :effort/endDate :effort/description :effort/task :effort/assignee])

(defn pre-process [effort]
  (-> effort
      (tu/add-namespace :effort)
      (select-keys effort-keys)))

(defn post-process [effort]
  (-> effort
      (update :effort/assignee #(get % :db/id))
      (update :effort/task #(get % :db/id))
      tu/remove-namespace))

(defn create-effort [effort]
  (d/transact conn [(pre-process effort)]))

(defn read-efforts []
  (->> @conn
       (d/q '[:find [(pull ?e [*]) ...] :where [?e :effort/reference ?r]])
       (mapv post-process)))

(defn read-effort-by-id [id]
  (-> @conn
      (d/pull '[*] id)
      post-process))

(defn read-effort-by-reference [reference]
  (read-effort-by-id [:effort/reference reference]))

(defn update-effort [{:keys [:db/id] :as effort}]
  (if-not id
    (throw (ex-info "id should not be nil" effort))
    (d/transact conn [(select-keys effort (conj effort-keys :db/id))])))

(defn delete-effort [id]
  (d/transact conn [[:db/retractEntity id]]))
