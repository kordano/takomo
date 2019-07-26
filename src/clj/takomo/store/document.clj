(ns takomo.store.document
  (:require [datahike.api :as d]
            [takomo.store :refer [store]]
            [takomo.utils :as tu]
            [clj-time.core :as t]
            [hasch.core :as h]))

(def document-keys [:document/createdAt :document/reference :document/fileName])

(defn create-document [document]
  (d/transact! (:conn @store) [(select-keys document document-keys)]))

(defn post-process [doc]
  (update doc :document/createdAt tu/format-to-iso-8601-date))

(defn read-documents []
  (->>
   (d/q '[:find [(pull ?e [:db/id :document/createdAt :document/reference :document/fileName])]
          :where [?e :document/reference ?r]]
        (d/db (:conn @store)))
   (mapv post-process)))

(defn read-document-by-id [id]
  (-> (d/db (:conn @store))
      (d/pull  '[:db/id
                 :document/createdAt
                 :document/reference
                 :document/fileName] id)
      post-process))

(defn read-document-by-reference [reference]
  (read-document-by-id [:document/reference reference]))

(defn update-document [{:keys [:db/id] :as document}]
  (if-not id
    (throw (ex-info "id should not be nil" {:db/id id}))
    (d/transact! (:conn @store) [(-> document
                                     (select-keys (conj document-keys :db/id)))])))

(defn delete-document [id]
  (d/transact! (:conn @store) [[:db/retractEntity id]]))

(comment


  )
