(ns takomo.login
  (:require [takomo.components :refer [field]]
            [cljs.reader :refer [read-string]]
            [accountant.core :as acc]
            [ajax.core :refer [POST]]))

(defn login-page [state]
  [:div.container.is-half
   [:h1.title "Welcome to TAKOMO"]
   [:p.subtitle "Please enter your credentials"]
   [field state :login :email :email "Email" "e.g. alice@umbrella.corp"]
   [field state :login :password :password "Password" "aliceisawesome!1"]
   [:a.button.is-primary
    {:on-click
     (fn []
       (POST "http://localhost:3000/api/login"
           {:handler (fn [response]
                       (swap! state assoc :credentials (read-string (str response)))
                       (swap! state assoc-in [:inputs :login] nil)
                       (acc/navigate! "/"))
            :error-handler #(js/alert (read-string (str %)))
            :response-format :json
            :format :json
            :params {:username (-> @state :inputs :login :email)
                     :password (-> @state :inputs :login :password)}
            :keywords? true}))}
    "Login"]])
