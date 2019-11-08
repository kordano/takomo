(ns takomo.pages.home
  (:require [takomo.network :as net]
            [cljs.reader :refer [read-string]]))

(defn home-page [state]
(net/api-get state
             "self/tasks"
            {}
            (fn [resp] (swap! state assoc-in [:self :tasks] (read-string (str resp)))))
  (let [table-data {:title "Title"
    :description "Description"
    :reference "Reference"
    :estimation "Estimation"
    :assignee "Assignee"
    :unit "Unit"
    :project "Project"}]
    [:div.container
     [:h1.title "Takomo"]
     [:p.subtitle "Lambdaforge ERP Services"]
     [:section.section
      [:div.box
       [:h1.title "My Tasks"]
       [:table.table
        [:thead
         [:tr (map (fn [[k v]] [:th {:key v} v]) table-data) [:th ""]]]
        [:tbody
         (map (fn [{:keys [id] :as el}]
                [:tr {:key id}
                 (map
                  (fn [[k v]] [:td {:key (or (str (get el k) k) (str id k))} (or (get el k) "-")])
                  table-data)
                 [:td
                  [:div
                   [:a
                    {:on-click (fn [] (js/alert (str "Start working: " el)))}
                    [:span.icon [:i.fas.fa-play.has-text-dark]]]
                   ]]])
              (-> @state :self :tasks))]]
        ]]]))
