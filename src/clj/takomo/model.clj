(ns takomo.model
  (:require [clojure.spec.alpha :as s])
  (:import [java.lang Long]))

(defn long? [x]
  (instance? Long x))

(s/def :db/id int?)

(s/def ::id int?)

(s/def :member/firstname string?)
(s/def :member/lastname string?)
(s/def :member/email string?)
(s/def :member/salt string?)
(s/def :member/passhash string?)
(s/def :member/password string?)
(s/def ::member (s/keys :opt [:db/id
                              :member/firstname
                              :member/lastname
                              :member/email
                              :member/password]))
(s/def ::members (s/coll-of ::member))

(s/def :customer/name string?)
(s/def :customer/contact string?)
(s/def :customer/department string?)
(s/def :customer/city string?)
(s/def :customer/street string?)
(s/def :customer/postal string?)
(s/def :customer/country string?)
(s/def ::customer (s/keys :opt [:db/id
                                :customer/name
                                :customer/contract
                                :customer/department
                                :customer/city
                                :customer/street
                                :customer/postal
                                :customer/country]))
(s/def ::customers (s/coll-of ::customer))