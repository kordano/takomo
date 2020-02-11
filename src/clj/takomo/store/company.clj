(ns takomo.store.company
  (:require [datahike.api :as d]
            [takomo.store :refer [conn]]
            [takomo.utils :refer [add-namespace remove-namespace]]))

(def company-keys [:company/name
                   :company/contact
                   :company/email
                   :company/phone
                   :company/department
                   :company/postal
                   :company/city
                   :company/street
                   :company/role
                   :company/postal
                   :company/country])

(defn pre-process [company]
  (-> company
      (add-namespace :company)
      (select-keys company-keys)))

(defn post-process [company]
  (-> company
      remove-namespace))

(defn create-company [new-company]
  (d/transact conn [(pre-process new-company)]))

(defn read-companies []
  (->>
       (d/q '[:find [(pull ?e ?selection) ...]
              :in $ ?selection
              :where [?e :company/name ?name]]
            @conn company-keys)
       (mapv post-process)))

(defn read-company-by-id [id]
  (-> @conn
      (d/pull '[:db/id :company/name
                :company/contact
                :company/department
                :company/postal
                :company/city
                :company/street
                :company/postal
                :company/country] id)
      post-process))

(defn read-company-by-name [name]
  (read-company-by-id [:company/name name]))

(defn update-company [{:keys [:db/id] :as company}]
  (if-not id
    (throw (ex-info "id should not be nil" {:db/id id}))
    (d/transact conn [(-> company
                          pre-process
                          (assoc :db/id id))])))

(defn delete-company [id]
  (d/transact conn [[:db/retractEntity id]]))
