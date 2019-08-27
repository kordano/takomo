(ns takomo.store.member
  (:require [datahike.api :as d]
            [hasch.core :as h]
            [takomo.utils :as tu]
            [takomo.store :refer [get-db get-conn]]))

(def member-keys [:member/firstname :member/lastname :member/email :member/role])

(defn pre-process [member]
  (-> member
      (tu/add-namespace :member)
      (select-keys member-keys)
      (merge
       {:member/salt     "123"
        :member/passhash (h/b64-hash (:password member))})))

(defn post-process [member]
  (-> member
      tu/remove-namespace))

(defn create-member [member]
  (d/transact (get-conn) [(pre-process member)]))

(defn read-members []
  (->> (get-db)
       (d/q '[:find [(pull ?e [:db/id :member/firstname :member/lastname :member/email]) ...]
              :where [?e :member/email ?email]])
       (mapv post-process)))

(defn read-member-by-id [id]
  (-> (get-db)
      (d/pull '[:db/id :member/firstname :member/lastname :member/email] id)
      post-process))

(defn read-member-by-email [email]
  (read-member-by-id [:member/email email]))

(defn update-member [{:keys [db/id] :as member}]
  (if-not id
    (throw (ex-info "id should not be nil" {:id id}))
    (d/transact (get-conn) [(-> member
                                pre-process
                                (assoc :db/id id)
                                 
                                 )])))

(defn delete-member [id]
  (d/transact (get-conn) [[:db/retractEntity id]]))
