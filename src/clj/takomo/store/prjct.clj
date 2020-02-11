(ns takomo.store.prjct
  (:require [datahike.api :as d]
            [datahike.core :as dc]
            [clojure.string :refer [split upper-case]]
            [takomo.store :refer [conn]]
            [takomo.utils :as tu]))

(def project-initial-keys
  [:project/title
   :project/description
   :project/client
   :project/startDate
   :project/endDate
   :project/responsible
   :project/members
   :project/rate
   :project/unit])

(def project-optional-keys [:project/acceptedAt :project/invoice :project/offers :project/paidAt :project/reference])

(defn create-reference [title]
  (let [title-letters (split title #"\s")]
    (loop [reference (if (< (count title-letters) 2)
                       (-> (str (first title) (last title))
                           (upper-case))
                       (->> title-letters
                            (map (comp first upper-case))
                            (apply str)))
           i 1]
      (if-not (dc/entid @conn [:project/reference reference])
        reference
        (recur (if (< (count title-letters) 2)
                 (-> (str (first title) (last title))
                     (upper-case)
                     (str i))
                 (str (->> title-letters
                           (map (comp first upper-case))
                           (apply str)) i))
               (inc i))))))

(defn pre-process [{:keys [title startDate endDate unit] :as project}]
  (let [new-project (-> project
                        (tu/add-namespace :project)
                        (select-keys project-initial-keys)
                        (assoc :project/reference (create-reference title))
                        (update :project/rate long))
        new-project (if startDate
                      (update new-project :project/startDate tu/str->Date)
                      new-project)
        new-project (if unit
                      (clojure.set/rename-keys new-project {:project/unit :project.rate/unit})
                      new-project)]
    (if endDate
      (update new-project :project/endDate tu/str->Date)
      new-project)))


(defn create-project [project]
  (d/transact conn [(pre-process project)]))

(defn post-process [{:keys [:project/startDate :project/endDate] :as project}]
  (let [new-project (-> project
                        (update :project/members #(mapv :db/id %))
                        (update :project/client #(get % :db/id))
                        (update :project/responsible #(get % :db/id))
                        tu/remove-namespace)
        new-project (if startDate
                      (update new-project :startDate tu/format-to-iso-8601-date)
                      new-project)]
    (if endDate
                      (update new-project :endDate tu/format-to-iso-8601-date)
                      new-project)))

(defn read-projects []
  (->> @conn
       (d/q '[:find [(pull ?e [*]) ...]
              :where
              [?e :project/reference ?r]])
       (mapv post-process)))

(defn read-project-by-id [id]
  (-> @conn
      (d/pull '[*] id)
      post-process))

(defn read-project-by-reference [reference]
  (read-project-by-id [:project/reference reference]))

(defn update-project [{:keys [db/id] :as project}]
  (if-not id
    (throw (ex-info "id should not be nil" project))
    (d/transact conn [(select-keys project (-> (concat project-initial-keys project-optional-keys)
                                               (conj :db/id)))])))

(defn delete-project [id]
  (d/transact conn [[:db/retractEntity id]]))
