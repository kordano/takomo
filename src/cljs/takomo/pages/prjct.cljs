(ns takomo.pages.prjct
  (:require [takomo.pages.templates :refer [overview-template]]
            [takomo.network :as net]
            [cljs.reader :refer [read-string]]))

(defn input-keys [state]
  {:title {:input-type :text
           :label "Title"
           :placeholder "e.g. Musterprojekt"}
   :description {:input-type :text
                 :label "Description"
                 :placeholder "e.g. App-Development"}
   :responsible {:input-type :select
                 :label "Responsible"
                 :allowed-values (mapv
                                  (fn [{:keys [id firstname lastname]}]
                                    {:id id :label (str firstname " " lastname)})
                                  (:members @state))}
   :startDate {:input-type :date
               :label "Start date"}
   :endDate {:input-type :date
             :label "End date"
             :placeholder "e.g. 1.2.2020"}
   :company {:input-type :select
             :label "company"
             :allowed-values (mapv
                              (fn [{:keys [id name]}]
                                {:label name :id id})
                              (:companies @state))}
   :members {:input-type :multi-select
             :label "Members"
             :allowed-values (mapv
                              (fn [{:keys [id firstname lastname]}]
                                {:id id :label (str firstname " " lastname)})
                              (:members @state))}
   :rate {:input-type :currency
          :label "Rate"
          :placeholder "100"}
   :unit {:input-type :select
          :label "Rate unit"
          :allowed-values [{:id "day" :label "Day"}
                           {:id "month" :label "Month"}
                           {:id "year" :label "Year"}]}})


(defn projects-page [state]
  (net/api-get state "members" {} (fn [resp] (swap! state assoc :members (read-string (str resp)))))
  (net/api-get state "companies" {} (fn [resp] (swap! state assoc :companies (read-string (str resp)))))
  [overview-template
   state
   "project"
   {:title "Title"
    :description "Description"
    :responsible "Responsible"
    :reference "Reference"}
   (input-keys state)])
