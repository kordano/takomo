(ns takomo.menu
  (:require [accountant.core :as acc]))

(defn menu [state]
  (let [token? (-> @state :credentials :token)]
    [:div.container
     [:aside.menu
      [:p.menu-label "General"]
      [:ul.menu-list
       [:li [:a {:on-click #(acc/navigate! "/")} "Home"]]
       (when-not token?
         [:li [:a {:on-click #(acc/navigate! "/login")} "Login"]])]
      (when token?
        [:p.menu-label "Members"])
      (when token?
        [:ul.menu-list
         [:li [:a {:on-click #(acc/navigate! "/members")} "Overview"]]
         [:li [:a {:on-click #(acc/navigate! "/new-member")} "Create new member"]]])
      (when token?
        [:p.menu-label ""])
      (when token?
        [:ul.menu-list
         [:li
          [:a
           {:style {:color "red"} :on-click (fn []
                                              (reset! state {})
                                              (acc/navigate! "/"))}
           "Logout"]]])]]))
