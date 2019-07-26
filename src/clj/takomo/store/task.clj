(ns takomo.store.task
  (:require [datahike.api :as d]
            [takomo.store :refer [store] :as s]
            [takomo.utils :as tu]
            [clj-time.core :as t]
            [hasch.core :as h]))

(def task-keys [:task/title :task/description :task/assignees :task/project :task/estimation :task/estimationUnit :task/reference])

(defn create-task [task]
  (d/transact! (:conn @store) [(select-keys task task-keys)]))

(defn read-tasks []
  (d/q '[:find [(pull ?e [*]) ...]
         :where
         [?e :task/title ?t]] (d/db (:conn @store))))

(defn read-task-by-id [id]
  (d/pull (d/db (:conn @store)) '[*] id))

(defn read-task-by-reference [reference]
  (read-task-by-id [:task/reference reference]))

(defn update-task [{:keys [db/id] :as task}]
  (if-not id
    (throw (ex-info "Id should not be nil" task))
    (d/transact! (:conn @store) [(select-keys task task-keys)])))

(defn delete-task [id]
  (d/transact! (:conn @store) [[:db/retractEntity id]]))

