(ns takomo.member
  (:require [ajax.core :refer [GET PUT DELETE POST]]
            [takomo.pages.templates :refer [creation-template overview-template details-template]]
            [takomo.components :refer [field]]
            [accountant.core :as acc]
            [cljs.reader :refer [read-string]]))

(def input-keys
 {:firstname [:text "Firstname" "e.g. Alice"]
  :lastname [:text "Lastname" "e.g. Abernathy"]
  :email [:email "Email" "e.g. alice@umbrella.corp"]
  :password [:password "Password" "e.g. alicerocks!1"]} )

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
    :email "Email"}])

(defn member-page [state]
  [details-template
   state
   "member"
   input-keys])
