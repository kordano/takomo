(ns takomo.pages.member
  (:require [takomo.pages.templates :refer [creation-template overview-template details-template]]))

(def input-keys
  {:firstname {:input-type :text
               :label "Firstname"
               :placeholder "e.g. Max"}
   :lastname {:input-type :text
              :label "Lastname"
              :placeholder "e.g. Mustermann"}
   :email {:input-type :email
           :label "Email"
           :placeholder "e.g. m.mustermann@musterfirma.de"}
   :password {:input-type :password
              :label "Password"
              :placeholder "e.g. nichtsehrsicher"}
   :role {:input-type :select
          :label "Role"
          :placeholder "Select role"
          :allowed-values [{:label "guest" :id "guest"} 
                           {:label "employee" :id "employee"}
                           {:label "manager" :id "manager"}
                           {:label "admin" :id "admin"}]}})

(defn new-member-page [state]
  [creation-template
   state
   "member"
   input-keys])

(defn members-page [state]
  [overview-template
   state
   "member"
   {:firstname "Firstname"
    :lastname "Lastname"
    :email "Email"
    :role "Role"}])

(defn member-page [state]
  [details-template
   state
   "member"
   input-keys])
