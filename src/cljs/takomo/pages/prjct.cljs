(ns takomo.pages.prjct
  (:require [takomo.pages.templates :refer [creation-template]]
            [takomo.network :as net]
            [cljs.reader :refer [read-string]]))

(defn input-keys [state]
  {:title {:input-type :text
           :label "Title"
           :placeholder "e.g. Musterprojekt"}
   :description {:input-type :text
                 :label "Description"
                 :placeholder "e.g. App-Development"}
   :responsible {:input-type :id-select
                 :label "Responsible"
                 :allowed-values (mapv
                                  (fn [{:keys [id firstname lastname]}]
                                    [id (str firstname " " lastname)])
                                  (:members @state))}
   :startDate {:input-type :date
               :label "Start date"
               :placeholder "e.g. 1.1.2020"}
   :endDate {:input-type :date
             :label "End date"
             :placeholder "e.g. 1.2.2020"}
   :acceptedAt {:input :date
                :label "Accepted at"
                :placeholder "e.g. 1.11.2019"}
   :customer {:input-type :id-select
              :label "Customer"
              :allowed-values (mapv
                               (fn [{:keys [id name]}]
                                 [id name])
                               (:customers @state))}
   :members {:input-type :id-select
             :label "Members"
             :allowed-values (mapv
                                  (fn [{:keys [id firstname lastname]}]
                                    [id (str firstname " " lastname)])
                                  (:members @state))}
   :rate {:input-type :number
          :label "Rate"
          :placeholder "100"}
   :unit {:input-type :select
          :label "Rate unit"
          :allowed-values ["day" "month" "year"]}})

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
   (input-keys state)
   ])

