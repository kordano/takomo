(ns takomo.menu
  (:require [accountant.core :as acc]
            [takomo.util :refer [logout]]))

(defn menu [state]
  (let [allowed-admin-roles #{"admin" "manager"}]
    [:div.container
     [:aside.menu
      [:p.menu-label "General"]
      [:ul.menu-list
       [:li [:a {:on-click #(acc/navigate! "/")} "Dashboard"]]]
      (when (allowed-admin-roles (-> @state :credentials :role))
        [:p.menu-label "Administration"])
      (when (allowed-admin-roles (-> @state :credentials :role))
        [:ul.menu-list
         [:li [:a {:on-click #(acc/navigate! "/members")} "Members"]]
         [:li [:a {:on-click #(acc/navigate! "/companies")} "companies"]]
         [:li [:a {:on-click #(acc/navigate! "/projects")} "Projects"]]
         [:li [:a {:on-click #(acc/navigate! "/tasks")} "Tasks"]]])
      [:p.menu-label ""]
      [:ul.menu-list
       [:li
        [:a.has-text-danger
         {:on-click (fn [] (logout state))}
         "Logout"]]]]]))
