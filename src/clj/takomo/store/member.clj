(ns takomo.store.member
  (:require [datahike.api :as d]
            [buddy.hashers :as bh]
            [takomo.utils :as tu]
            [takomo.store :refer [conn]]))

(def member-keys [:member/firstname :member/lastname :member/email :member/role])

(defn pre-process [member]
  (-> member
      (tu/add-namespace :member)
      (update :member/role (fn [old] (keyword "member" old)))
      (select-keys member-keys)
      (merge
       {:member/password (bh/derive (:password member))})))

(defn post-process [member]
  (-> member
      (update :member/role #(-> % :db/ident name keyword))
      tu/remove-namespace))

(defn create-member [member]
  (d/transact conn [(pre-process member)]))

(defn read-members []
  (->> @conn
       (d/q '[:find [(pull ?e [:db/id :member/firstname :member/lastname :member/email {:member/role [:db/ident]}]) ...]
              :where [?e :member/email ?email]])
       (mapv post-process)))

(defn read-member-by-id [id]
  (-> @conn
      (d/pull '[:db/id :member/firstname :member/lastname :member/email {:member/role [:db/ident]}] id)
      post-process))

(defn read-member-by-email [email]
  (read-member-by-id [:member/email email]))

(defn update-member [{:keys [db/id] :as member}]
  (if-not id
    (throw (ex-info "id should not be nil" {:id id}))
    (d/transact conn [(-> member
                          pre-process
                          (assoc :db/id id))])))

(defn delete-member [id]
  (d/transact conn [[:db/retractEntity id]]))

(defn credentials-valid? [username password]
  (if-let [member-password (:member/password (d/pull @conn '[:member/password] [:member/email username]))]
    (bh/check password member-password)
    (throw (ex-info "Invalid Credentials" {:username username :password password}))))

