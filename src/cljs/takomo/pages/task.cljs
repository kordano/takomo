(ns takomo.pages.task
  (:require [takomo.pages.templates :refer [ overview-template]]
            [takomo.network :as net]
            [cljs.reader :refer [read-string]]))

(defn input-keys [state]
  {:title {:input-type :text
           :label "Title"
           :placeholder "e.g. Musteraufgabe"}
   :description {:input-type :text
                 :label "Description"
                 :placeholder "e.g. Musterbeschreibung"}
   :assignee {:input-type :select
               :label "Assignee"
               :allowed-values (mapv (fn [{:keys [id firstname lastname]}]
                                       {:id id
                                        :label (str firstname " " lastname)})
                                     (:members @state))}
   :estimation {:input-type :float
                :label "Estimation"
                :placeholder "e.g. 100.50"}
   :unit {:input-type :select
          :label "Unit"
          :allowed-values [{:id "hour" :label "Hour"}
                           {:id "day" :label "Day"}
                           {:id "month" :label "Month"}
                           {:id "year" :label "Year"}
                           ]}
   :project {:input-type :select
             :label "Project"
             :allowed-values (mapv (fn [{:keys [id title]}]
                                     {:id id :label title}) (:projects @state))}
   })



(defn tasks-page [state]
  (net/api-get state "members" {} (fn [resp] (swap! state assoc :members (read-string (str resp)))))
  (net/api-get state "projects" {} (fn [resp] (swap! state assoc :projects (read-string (str resp)))))
  [overview-template
   state
   "task"
   {:title "Title"
    :description "Description"
    :reference "Reference"
    :estimation "Estimation"
    :assignee "Assignee"
    :unit "Unit"
    :project "Project"}
   (input-keys state)])
