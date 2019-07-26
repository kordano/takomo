(ns takomo.store.task
  (:require [datahike.api :as d]
            [takomo.store :refer [get-conn get-db]]
            [takomo.utils :as tu]
            [clj-time.core :as t]
            [hasch.core :as h]))

(def task-keys [:task/title :task/description :task/assignees :task/project :task/estimation :task.estimation/unit :task/reference])

(defn preprocess [task]
  (-> task
      (update :task/estimation double)))

(defn create-task [task]
  (d/transact! (get-conn) [(-> task
                               (select-keys task-keys)
                               preprocess)]))

(defn postprocess [task]
  (-> task
      (update :task/assignees #(mapv :db/id %))
      (update :task/project #(get % :db/id))))

(defn read-tasks []
  (->> (get-db)
       (d/q '[:find [(pull ?e [*]) ...]
              :where
              [?e :task/title ?t]] )
       (mapv postprocess)))

(read-tasks)

(defn read-task-by-id [id]
  (-> (get-db)
      (d/pull  '[*] id)
      postprocess))

(defn read-task-by-reference [reference]
  (read-task-by-id [:task/reference reference]))

(defn update-task [{:keys [db/id] :as task}]
  (if-not id
    (throw (ex-info "Id should not be nil" task))
    (d/transact! (get-conn) [(select-keys task (conj task-keys :db/id))])))

(defn delete-task [id]
  (d/transact! (get-conn) [[:db/retractEntity id]]))
