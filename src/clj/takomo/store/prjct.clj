(ns takomo.store.prjct
  (:require [datahike.api :as d]
            [datahike.core :as dc]
            [clojure.string :refer [split upper-case join]]
            [takomo.store :refer [get-db get-conn]]
            [takomo.utils :as tu]))

(def project-initial-keys
  [:project/title :project/description :project/startDate :project/endDate :project/customer
   :project/responsible :project/members :project/invoice :project/offers :project/rate
   :project.rate/unit ])

(def project-optional-keys [:project/acceptedAt :project/paidAt :project/reference])

(defn create-reference [title]
  (let [title-letters (split title #"\s")]
    (loop [reference (if (< (count title-letters) 2)
                    (-> (str (first title) (last title))
                        (upper-case))
                    (->> title-letters
                         (map (comp first upper-case))
                         (apply str)))
           i 1]
      (if-not (dc/entid (get-db) [:project/reference reference])
        reference
        (recur (if (< (count title-letters) 2)
                    (-> (str (first title) (last title))
                        (upper-case)
                        (str i))
                    (str (->> title-letters
                              (map (comp first upper-case))
                              (apply str)) i))
               (inc i))))))

(defn preprocess [{:keys [:project/title] :as project}]
  (-> project
      (assoc :project/reference (create-reference title))
      (update :project/rate long)
      tu/remove-namespace))


(defn create-project [project]
  (println (class (:project/rate project)))
  (d/transact! (get-conn) [(-> project
                               (select-keys project-initial-keys)
                               preprocess)]))

(defn postprocess [project]
  (-> project
      (update :project/members #(mapv :db/id %))
      (update :project/customer #(get % :db/id))
      (update :project/responsible #(get % :db/id))
      tu/remove-namespace))

(defn read-projects []
  (->> (get-db)
       (d/q '[:find [(pull ?e [*]) ...]
              :where
              [?e :project/reference ?r]])
       (mapv postprocess)))

(defn read-project-by-id [id]
  (-> (get-db)
      (d/pull '[*] id)
      postprocess))

(defn read-project-by-reference [reference]
  (read-project-by-id [:project/reference reference]))

(defn update-project [{:keys [db/id] :as project}]
  (if-not id
    (throw (ex-info "id should not be nil" project))
    (d/transact! (get-conn) [(select-keys project (-> (concat project-initial-keys project-optional-keys)
                                                      (conj :db/id)))])))

(defn delete-project [id]
  (d/transact! (get-conn) [[:db/retractEntity id]]))

(comment

  (create-project {:project/title "boofar" :project/rate 100 :project/customer 44 :project/responsible 43 :project/members [43]})

  (read-projects)


  )
