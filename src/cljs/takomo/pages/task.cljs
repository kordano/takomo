(ns takomo.pages.task
  (:require [takomo.pages.templates :refer [creation-template overview-template details-template]]
            [takomo.network :as net]
            [cljs.reader :refer [read-string]]))

(defn input-keys [state]
  {:title {:input-type :text
           :label "Title"
           :placeholder "e.g. Musteraufgabe"}
   :description {:input-type :text
                 :label "Description"
                 :placeholder "e.g. Musterbeschreibung"}
   :assignees {:input-type :multi-select
               :label "Assignees"
               :allowed-values (mapv (fn [{:keys [id firstname lastname]}]
                                       {:id id
                                        :label (str firstname " " lastname)})
                                     (:members @state))}
   :estimation {:input-type :float
                :label "Estimation"
                :placeholder "e.g. 100.50"}
   :unit {:input-type :select
          :label "Unit"
          :allowed-values [{:id "day" :label "Day"}
                           {:id "month" :label "Month"}
                           {:id "year" :label "Year"}
                           ]}
   :project {:input-type :select
             :label "Project"
             :allowed-values (mapv (fn [{:keys [id title]}]
                                     {:id id :label title}) (:projects @state))}
   })

(defn new-task-page [state]
  (net/api-get state
             "members"
             {}
             (fn [resp] (swap! state assoc :members (read-string (str resp)))))
  (net/api-get state
             "projects"
             {}
             (fn [resp] (swap! state assoc :projects (read-string (str resp)))))
  [creation-template
   state
   "task"
   (input-keys state)])

(defn tasks-page [state]
  [overview-template
   state
   "task"
   {:title "Title"
    :description "Description"
    :assignees "Assignees"
    :reference "Reference"
    :estimation "Estimation"
    :project "Project"}])

(defn task-page [state]
  (net/api-get state
             "members"
             {}
             (fn [resp] (swap! state assoc :members (read-string (str resp)))))
  (net/api-get state
             "projects"
             {}
             (fn [resp] (swap! state assoc :projects (read-string (str resp)))))
  [details-template
   state
   "task"
   (input-keys state)])
