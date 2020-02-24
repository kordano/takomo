(ns takomo.pages.assets
  (:require [takomo.pages.templates :refer [overview-template]]))


(defn assets-page [state]
  [overview-template
   state
   "asset"
   {:reference "Reference"
    :description "Description"
    :price "Price"
    :boughtAt "Bought At"}
   {:reference {:input-type :text
                :label "Reference"
                :placeholder "e.g. ABC-123"}
    :description {:input-type :text
                  :label "Description"
                  :placeholder "e.g. Laptop"}
    :price {:input-type :currency
            :label "Price"
            :placeholder "e.g. 100"}
    :boughtAt {:input-type :date
               :label "Bought at"}
    }])
