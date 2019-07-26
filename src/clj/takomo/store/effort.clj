(ns takomo.store.effort
(:require [datahike.api :as d]
          [takomo.store :refer [get-db]]
          [takomo.utils :as tu]
          [clj-time.core :as t]
          [hasch.core :as h]))

(def effort-keys [:effort/startDate :effort/reference :effort/endDate :effort/description :effort/task :effort/assignee])

(defn pre-process-tx [{:keys [:effort/task] :as effort}]
  (if task
    (let [task-ref (d/pull )])
    effort))

(defn create-effort [effort]
  (d/transact! (:conn @store) [(select-keys effort effort-keys)]))


(defn read-efforts []
  (d/q '[:find [(pull ?e [*]) ...]
         :where [?e :effort/reference ?r]]
       (d/db (:conn @store))))

(defn read-effort-by-id [id]
  (d/pull (d/db (:conn @store)) '[*] id))

(defn read-effort-by-reference [reference]
  (read-effort-by-id [:effort/reference reference]))

(defn update-effort [{:keys [:db/id] :as effort}]
  (if-not id
    (throw (ex-info "id should not be nil" effort))
    (d/transact! (:conn @store) [(select-keys effort (conj effort-keys :db/id))])))

(defn delete-effort [id]
  (d/transact! (:conn @store) [[:db/retractEntity id]]))
