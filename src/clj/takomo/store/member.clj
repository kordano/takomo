(ns takomo.store.member
  (:require [datahike.api :as d]
            [takomo.store :refer [get-db get-conn]]))

(def member-keys [:member/firstname :member/lastname :member/email :member/role])

(defn create-member [{:keys [:member/password] :as new-member}]
  (d/transact! (get-conn) [(-> new-member
                       (select-keys member-keys)
                       (merge
                        {:member/salt     "123"
                         :member/passhash (h/b64-hash password)}))]))

(defn read-members []
  (d/q '[:find [(pull ?e [:db/id :member/firstname :member/lastname :member/email]) ...] :where [?e :member/email ?email]]
       (get-db)))

(defn read-member-by-id [id]
  (d/pull (get-db) '[:db/id :member/firstname :member/lastname :member/email] id))

(defn read-member-by-email [email]
  (read-member-by-id [:member/email email]))

(defn update-member [{:keys [:member/password :db/id] :as member }]
  (if-not id
    (throw (ex-info "id should not be nil" {:id id}))
    (d/transact! (get-conn) [(-> member
                         (select-keys (conj member-keys :db/id))
                         (merge
                          (when password
                            {:member/passhash (h/b64-hash password)})))])))

(defn delete-member [id]
  (d/transact! (get-conn) [[:db/retractEntity id]]))
