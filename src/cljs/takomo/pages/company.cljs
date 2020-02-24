(ns takomo.pages.company
  (:require [takomo.pages.templates :refer [overview-template]]))

(def input-data
  {:name {:input-type :text
          :label "Name"
          :placeholder "e.g. Musterfirma"}
   :contact {:input-type :text
             :label "Contact"
             :placeholder "e.g. Erika Musterfrau"}
   :email {:input-type :text
           :label "Emil"
           :placeholder "e.g. info@takomo.eu"}
   :phone {:input-type :text
           :label "Phone"
           :placeholder "e.g. 012345"}
   :department {:input-type :text
                :label "Department"
                :placeholder "e.g. R&D"}
   :role {:input-type :select
          :label "Role"
          :placeholder "Select role"
          :allowed-values [{:label "Regular" :id "regular"}
                           {:label "Contractor" :id "contractor"}
                           {:label "Partner" :id "partner"}]}
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

(defn companies-page [state]
  [overview-template
   state
   "company"
   {:name "Name"
    :contact "Contact"
    :email "Email"
    :phone "Phone"
    :role "Role"
    }
   input-data])
