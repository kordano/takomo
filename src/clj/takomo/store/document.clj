(ns takomo.store.document
  (:require [datahike.api :as d]
            [takomo.store :refer [get-db get-conn]]
            [takomo.utils :as tu]
            [clj-time.core :as t]
            [hasch.core :as h]))

(def document-keys [:document/reference :document/fileName])

(defn pre-process [document]
  (-> document
      (tu/add-namespace :document)
      (select-keys document-keys)))

(defn create-document [document]
  (d/transact (get-conn) [(pre-process document)]))

(defn post-process [doc]
  (-> doc
      (update :document/createdAt tu/format-to-iso-8601-date)
      tu/remove-namespace))

(defn read-documents []
  (->>
   (d/q '[:find [(pull ?e [*])]
          :where [?e :document/reference ?r]]
        (get-db))
   (mapv post-process)))

(defn read-document-by-id [id]
  (-> (get-db)
      (d/pull '[*] id)
      post-process))

(defn read-document-by-reference [reference]
  (read-document-by-id [:document/reference reference]))

(defn update-document [{:keys [:db/id] :as document}]
  (if-not id
    (throw (ex-info "id should not be nil" {:db/id id}))
    (d/transact (get-conn) [(-> document
                                pre-process
                                (assoc :db/id id))])))

(defn delete-document [id]
  (d/transact (get-conn) [[:db/retractEntity id]]))

