(ns takomo.login
  (:require [reagent.core :as r]
            [takomo.components :refer [field]]
            [cljs.reader :refer [read-string]]
            [accountant.core :as acc]
            [ajax.core :refer [POST]]))

(defn login-page [state]
  (let [inputs (r/atom {:username "admin@takomo.eu" :password "takomo"})]
    [:div.columns
     [:div.column.container.is-one-third.is-offset-one-third
      [:div.card
       [:div.card-content
        [:div.content
         [:h1.title "Welcome to TAKOMO"]
         [:p.subtitle "Please enter your credentials"]
         [field inputs :email :username "Email" "e.g. alice@umbrella.corp"]
         [field inputs :password :password "Password" "aliceisawesome!1"]
         [:a.button.is-primary
          {:on-click
           (fn []
             (POST "http://localhost:3000/api/login"
               {:handler (fn [response]
                           (swap! state assoc :credentials (read-string (str response)))
                           (reset! inputs nil)
                           (acc/navigate! "/"))
                :error-handler #(js/alert (read-string (str %)))
                :response-format :json
                :format :json
                :params @inputs
                :keywords? true}))}
          "Login"]]]]]]))
