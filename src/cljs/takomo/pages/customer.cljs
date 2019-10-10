(ns takomo.pages.customer
  (:require [ajax.core :refer [GET PUT DELETE POST]]
            [takomo.components :refer [field]]
            [takomo.pages.templates :refer [creation-template overview-template details-template]]
            [accountant.core :as acc]
            [cljs.reader :refer [read-string]]))

(def input-data 
   {:name [:text "Name" "e.g. Umbrella Corp"]
    :contact [:text "Contact" "e.g. Wesker"]
    :department [:text "Department" "e.g. R&D"]
    :city [:text "City" "e.g. Racoon City"]
    :street [:text "Street" "e.g. Main Avenue 42"]
    :postal [:text "Postal" "e.g. 3210"]
    :country [:text "Country" "e.g. USA"]})

(defn new-customer-page [state]
  [creation-template
   state
   "customer"
   input-data
   ])

(defn customers-page [state]
  [overview-template
   state
   "customer"
   {:name "Name"
    :contact "Contact"}])

(defn customer-page [state]
  [details-template
   state
   "customer"
   input-data
   ])