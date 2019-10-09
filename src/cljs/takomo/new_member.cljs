(ns takomo.new-member
  (:require [takomo.components :refer [field]]
            [cljs.reader :refer [read-string]]
            [ajax.core :refer [POST]]))

(defn new-member-page [state]
  [:div.container
   [:h1.title "Create new member"]
   [field state :new-member :text :firstname "Firstname" "e.g. Alice"]
   [field state :new-member :text :lastname "Lastname" "e.g. Abernathy"]
   [field state :new-member :email :email "Email" "e.g. alice@umbrella.corp"]
   [field state :new-member :password :password "Password" "e.g. alicerocks!1"]
   [:a.button.is-primary
    {:on-click
     (fn []
       (POST "http://localhost:3000/api/members"
           {:handler (fn []
                       (swap! state update-in [:notifications] conj "Member created!")
                       (swap! state assoc-in [:inputs :new-member] nil))
            :error-handler #(js/alert (read-string (str %)))
            :response-format :json
            :format :json
            :headers {"Authorization" (str "Token " (-> @state :credentials :token))}
            :params {:firstname (-> @state :inputs :new-member :firstname)
                     :lastname (-> @state :inputs :new-member :lastname)
                     :email (-> @state :inputs :new-member :email)
                     :password (-> @state :inputs :new-member :password)}
            :keywords? true}))}
    "Create"]])
