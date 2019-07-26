(ns takomo.store.customer
  (:require [datahike.api :as d]
            [takomo.store :refer [get-db get-conn]]))

(def customer-keys [:customer/name
                    :customer/contact
                    :customer/department
                    :customer/postal
                    :customer/city
                    :customer/street
                    :customer/postal
                    :customer/country])

(defn create-customer [new-customer]
  (d/transact! (get-conn) [(select-keys new-customer customer-keys)]))

(defn read-customers []
  (d/q '[:find [(pull ?e [:db/id :customer/name :customer/contact :customer/department :customer/postal :customer/city :customer/street :customer/postal :customer/country]) ...]
         :where [?e :customer/name ?name]]
       (get-db)))

(defn read-customer-by-id [id]
  (d/pull (get-db) '[:db/id :customer/name
               :customer/contact
               :customer/department
               :customer/postal
               :customer/city
               :customer/street
               :customer/postal
               :customer/country ] id))

(defn read-customer-by-name [name]
  (read-customer-by-id [:customer/name name]))

(defn update-customer [{:keys [:db/id] :as customer}]
  (if-not id
    (throw (ex-info "id should not be nil" {:db/id id}))
    (d/transact! (get-conn) [(-> customer
                         (select-keys (conj customer-keys :db/id )))])))

(defn delete-customer [id]
  (d/transact! (get-conn) [[:db/retractEntity id]]))
