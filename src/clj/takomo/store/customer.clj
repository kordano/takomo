(ns takomo.store.customer
  (:require [datahike.api :as d]
            [takomo.store :refer [get-db get-conn]]
            [takomo.utils :refer [add-namespace remove-namespace]]))

(def customer-keys [:customer/name
                    :customer/contact
                    :customer/department
                    :customer/postal
                    :customer/city
                    :customer/street
                    :customer/postal
                    :customer/country])

(defn pre-process [customer]
  (-> customer
      (add-namespace :customer)
      (select-keys customer-keys)))

(defn post-process [customer]
  (-> customer
      remove-namespace))

(defn create-customer [new-customer]
  (d/transact (get-conn) [(pre-process new-customer)]))

(defn read-customers []
  (->> (get-db)
       (d/q '[:find [(pull ?e [:db/id :customer/name :customer/contact :customer/department :customer/postal :customer/city :customer/street :customer/postal :customer/country]) ...]
              :where [?e :customer/name ?name]])
       (mapv post-process)))

(defn read-customer-by-id [id]
  (-> (get-db)
      (d/pull '[:db/id :customer/name
                :customer/contact
                :customer/department
                :customer/postal
                :customer/city
                :customer/street
                :customer/postal
                :customer/country] id)
      post-process))

(defn read-customer-by-name [name]
  (read-customer-by-id [:customer/name name]))

(defn update-customer [{:keys [:db/id] :as customer}]
  (if-not id
    (throw (ex-info "id should not be nil" {:db/id id}))
    (d/transact (get-conn) [(-> customer
                                pre-process
                                (assoc :db/id id))])))

(defn delete-customer [id]
  (d/transact (get-conn) [[:db/retractEntity id]]))
