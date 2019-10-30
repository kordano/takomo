(ns takomo.pages.prjct
  (:require [takomo.pages.templates :refer [creation-template overview-template details-template]]
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
   :customer {:input-type :select
              :label "Customer"
              :allowed-values (mapv
                               (fn [{:keys [id name]}]
                                 {:label name :id id})
                               (:customers @state))}
   :members {:input-type :multi-select
             :label "Members"
             :allowed-values (mapv
                                  (fn [{:keys [id firstname lastname]}]
                                    {:id id :label (str firstname " " lastname)})
                                  (:members @state))}
   :rate {:input-type :number
          :label "Rate"
          :placeholder "100"}
   :unit {:input-type :select
          :label "Rate unit"
          :allowed-values [{:id "day" :label "Day"}
                           {:id "month" :label "Month"}
                           {:id "year" :label "Year"}]}})

(defn new-project-page [state]
  (net/api-get state
             "members"
             {}
             (fn [resp] (swap! state assoc :members (read-string (str resp)))))
  (net/api-get state
             "customers"
             {}
             (fn [resp] (swap! state assoc :customers (read-string (str resp)))))
  [creation-template
   state
   "project"
   (input-keys state)])

(defn projects-page [state]
  [overview-template
   state
   "project"
   {:title "Title"
    :description "Description"
    :responsible "Responsible"
    :reference "Reference"}])

(defn project-page [state]
  (net/api-get state
             "members"
             {}
             (fn [resp] (swap! state assoc :members (read-string (str resp)))))
  (net/api-get state
             "customers"
             {}
             (fn [resp] (swap! state assoc :customers (read-string (str resp)))))
  [details-template
   state
   "project"
   (input-keys state)])
