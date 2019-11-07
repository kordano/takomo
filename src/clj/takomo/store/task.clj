(ns takomo.store.task
  (:require [datahike.api :as d]
            [takomo.store :refer [conn]]
            [com.rpl.specter :as s]
            [takomo.utils :as tu]))

(def task-keys
  [:task/title
   :task/description
   :task/assignee
   :task/project
   :task/estimation
   :task/unit])

(defn create-task-ref [project-id]
  (let [project-ref (:project/reference (d/pull @conn '[:project/reference] project-id))]
    (loop [i 1]
      (let [task-ref (str project-ref "-" i)]
        (if-not (d/entity @conn [:task/reference task-ref])
          task-ref
          (recur (inc i)))))))

(defn pre-process [{:keys [unit project] :as task}]
  (let [new-task (-> task
                     (tu/add-namespace :task)
                     (select-keys task-keys)
                     (update :task/estimation double))
        new-task (if unit
                   (clojure.set/rename-keys new-task {:task/unit :task.estimation/unit})
                   new-task)
        new-task (if project
                   (assoc new-task :task/reference (create-task-ref project))
                   new-task)]
    new-task))

(defn post-process [task]
  (-> task
      (update :task/assignee :db/id)
      (update :task/project :db/id)
      tu/remove-namespace))

(defn create-task [task]
  (d/transact conn [(pre-process task)]))

(defn read-tasks []
  (->> @conn
       (d/q '[:find [(pull ?e [*]) ...]
              :where
              [?e :task/title ?t]])
       (s/transform [s/ALL s/MAP-VALS map?] tu/remove-namespace)
       (s/transform [s/ALL] tu/remove-namespace)
       (s/transform [s/ALL :project] :id)
       (s/transform [s/ALL :assignee] :id)
       ))

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
