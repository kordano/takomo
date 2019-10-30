(ns takomo.store.task
  (:require [datahike.api :as d]
            [takomo.store :refer [conn]]
            [takomo.utils :as tu]))

(def task-keys [:task/title :task/description :task/assignees :task/project :task/estimation :task.estimation/unit :task/reference])

(defn pre-process [task]
  (-> task
      (tu/add-namespace :task)
      (select-keys task-keys)
      (update :task/estimation double)))

(defn post-process [task]
  (-> task
      (update :task/assignees #(mapv :db/id %))
      (update :task/project #(get % :db/id))
      tu/remove-namespace))

(defn create-task [task]
  (d/transact conn [(pre-process task)]))

(defn read-tasks []
  (->> @conn
       (d/q '[:find [(pull ?e [*]) ...]
              :where
              [?e :task/title ?t]])
       (mapv post-process)))

(defn read-task-by-id [id]
  (-> @conn
      (d/pull  '[*] id)
      post-process))

(defn read-task-by-reference [reference]
  (read-task-by-id [:task/reference reference]))

(defn update-task [{:keys [db/id] :as task}]
  (if-not id
    (throw (ex-info "Id should not be nil" task))
    (d/transact conn [(select-keys task (conj task-keys :db/id))])))

(defn delete-task [id]
  (d/transact conn [[:db/retractEntity id]]))
