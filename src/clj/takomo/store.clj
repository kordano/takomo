(ns takomo.store
  (:require [clojure.spec.alpha :as s]
            [datahike.api :as d]
            [hasch.core :as h]))

(defonce state (atom {:uri  "datahike:mem://takomo"
                      :conn nil}))

(defn init []
  (let [uri (:uri @state)
        schema-tx (-> "resources/dh/schema.edn" slurp read-string)]
    (d/create-database {:uri uri :initial-tx schema-tx})
    (swap! state assoc :conn (d/connect uri))))

(def member-keys [:member/firstname :member/lastname :member/email :member/role])

(defn create-member [{:keys [:member/password] :as new-member}]
  (d/transact! (:conn @state) [(-> new-member
                                   (select-keys member-keys)
                                   (merge
                                     {:member/salt     "123"
                                      :member/passhash (h/b64-hash password)}))]))

(defn read-members []
  (d/q '[:find [(pull ?e [:db/id :member/firstname :member/lastname :member/email]) ...] :where [?e :member/email ?email]]
       (d/db (:conn @state))))

(defn read-member-by-id [id]
  (d/pull (d/db (:conn @state)) '[:db/id :member/firstname :member/lastname :member/email] id))

(defn read-member-by-email [email]
  (read-member-by-id [:member/email email]))

(defn update-member [{:keys [:member/password :db/id] :as member }]
  (if-not id
    (throw (ex-info "id should not be nil" {:id id}))
    (d/transact! (:conn @state) [(-> member
                                     (select-keys (conj member-keys :db/id))
                                     (merge
                                       (when password
                                         {:member/passhash (h/b64-hash password)})))])))

(defn delete-member [id]
  (d/transact! (:conn @state) [[:db/retractEntity id]]))

(def customer-keys [:customer/name
                    :customer/contact
                    :customer/department
                    :customer/postal
                    :customer/city
                    :customer/street
                    :customer/postal
                    :customer/country])

(defn create-customer [new-customer]
  (d/transact! (:conn @state) [(select-keys new-customer customer-keys)]))

(defn read-customers []
  (d/q '[:find [(pull ?e [:db/id :customer/name :customer/contact :customer/department :customer/postal :customer/city :customer/street :customer/postal :customer/country]) ...]
         :where [?e :customer/name ?name]]
       (d/db (:conn @state))))

(defn read-customer-by-id [id]
  (d/pull (d/db (:conn @state)) '[:db/id :customer/name
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
    (d/transact! (:conn @state) [(-> customer
                                     (select-keys (conj customer-keys :db/id )))])))

(defn delete-customer [id]
  (d/transact! (:conn @state) [[:db/retractEntity id]]))

(comment

  (d/delete-database (-> state deref :uri))

  (init)

  )
