(ns takomo.menu
  (:require [accountant.core :as acc]))

(defn menu [state]
  [:div.container
   [:aside.menu
    [:p.menu-label "General"]
    [:ul.menu-list
     [:li [:a {:on-click #(acc/navigate! "/")} "Home"]]]
    [:p.menu-label "Members"]
    [:ul.menu-list
     [:li [:a {:on-click #(acc/navigate! "/members")} "Overview"]]
     [:li [:a {:on-click #(acc/navigate! "/new-member")} "Create new member"]]]
    [:p.menu-label "Customers"]
    [:ul.menu-list
     [:li [:a {:on-click #(acc/navigate! "/customers")} "Overview"]]
     [:li [:a {:on-click #(acc/navigate! "/new-customer")} "Create new customer"]]]
    [:p.menu-label ""]
    [:ul.menu-list
     [:li
      [:a
       {:style {:color "red"} :on-click (fn []
                                          (reset! state {})
                                          (acc/navigate! "/"))}
       "Logout"]]]]])
