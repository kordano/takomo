(ns takomo.pages.login
  (:require [reagent.core :as r]
            [takomo.components :refer [field]]
            [cljs.reader :refer [read-string]]
            [accountant.core :as acc]
            [ajax.core :refer [POST]]))

(defn login-page [state]
  (let [inputs (r/atom {:username "mmustermann@takomo.eu" :password "employee"})]
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
                             (let [credentials (-> response str read-string (update :expired #(js/Date. %)))]
                               (swap! state assoc :credentials credentials)
                               (.setItem (.-localStorage js/window) "credentials" (str credentials)))
                           (reset! inputs nil)
                           (acc/navigate! "/"))
                :error-handler #(js/alert (read-string (str %)))
                :response-format :json
                :format :json
                :params @inputs
                :keywords? true}))}
          "Login"]]]]]]))
