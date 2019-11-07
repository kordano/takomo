(ns takomo.pages.customer
  (:require [takomo.pages.templates :refer [ overview-template ]]))

(def input-data 
  {:name {:input-type :text
          :label "Name"
          :placeholder "e.g. Musterfirma"}
   :contact {:input-type :text
             :label "Contact"
             :placeholder "e.g. Erika Musterfrau"}
   :department {:input-type :text
                :label "Department"
                :placeholder "e.g. R&D"}
   :city {:input-type :text
          :label "City"
          :placeholder "e.g. Musterstadt"}
   :street {:input-type :text
            :label "Street"
            :placeholder "e.g. Haupststrasse 42"}
   :postal {:input-type :text
            :label "Postal"
            :placeholder "e.g. 1234"}
   :country {:input-type :text
             :label "Country"
             :placeholder "e.g. Germany"}})

(defn customers-page [state]
  [overview-template
   state
   "customer"
   {:name "Name"
    :contact "Contact"}
   input-data])
