(ns takomo.pages.turnovers
  (:require [takomo.pages.templates :refer [overview-template]]))

(def input-data
  {:salesData {:input-type :text
               :label "Sales Data"
               :placeholder "e.g. Gehalt Konto 1234"}
   :bookingDay {:input-type :date
                 :label "Booking Date"}
   :amount {:input-type :currency
            :label "Amount"
            :placeholder "e.g. 498"}
   :remark {:input-type :text
            :label "Remark"
            :placeholder "e.g. refers to account 12324"}})

(defn turnovers-page [state]
  [overview-template
   state
   "turnover"
   {:bookingDay "Booking Day"
    :salesData "Sales Data"
    :amount "Amount"
    :remark "Remark"}
   input-data])
