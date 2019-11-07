(ns takomo.core
  (:require [reagent.core :as r]
            [takomo.pages.home :refer [home-page]]
            [takomo.menu :refer [menu]]
            [takomo.pages.member :refer [members-page member-page new-member-page]]
            [takomo.pages.customer :refer [new-customer-page customers-page customer-page]]
            [takomo.pages.login :refer [login-page]]
            [takomo.pages.prjct :refer [new-project-page projects-page project-page]]
            [takomo.pages.task :refer [new-task-page tasks-page task-page]]
            [cljs.reader :refer [read-string]]
            [accountant.core :as accountant]
            [secretary.core :as secretary :refer-macros [defroute]]
            [goog.events :as events]
            [goog.history.EventType :as HistoryEventType])
  (:import goog.History))

(defonce state (r/atom {}))

(defn get-headers []
    (let [token (-> @state :credentials :token)]
      {"Authorization" (str "Token " token)}))

(def selected-page (r/atom home-page))

(defn page []
  (if-not (-> @state :credentials :token)
    [:section.section
     [login-page state]]
    [:div.columns
     [:div.column.is-one-fifth
      [:section.section
       [menu state]]]
     [:div.column
      [:section.section
       [@selected-page state]]]]))

(defroute "/" []
  (reset! selected-page home-page))

(defroute "/members" []
  (reset! selected-page members-page))

(defroute "/member" []
  (reset! selected-page member-page))

(defroute "/new-member" []
  (reset! selected-page new-member-page))

(defroute "/customers" []
  (reset! selected-page customers-page))

(defroute "/customer" []
  (reset! selected-page customer-page))

(defroute "/new-customer" []
  (reset! selected-page new-customer-page))

(defroute "/new-project" []
  (reset! selected-page new-project-page))

(defroute "/projects" []
  (reset! selected-page projects-page))

(defroute "/project" []
  (reset! selected-page project-page))

(defroute "/new-task" []
  (reset! selected-page new-task-page))

(defroute "/tasks" []
  (reset! selected-page tasks-page))

(defroute "/task" []
  (reset! selected-page task-page))

(defn hook-browser-navigation! []
  (doto (History.)
        (events/listen
         HistoryEventType/NAVIGATE
         (fn [event]
           (secretary/dispatch! (.-token event))))
        (.setEnabled true)))

(defn mount-root []
  (r/render [page] (.getElementById js/document "root")))

(defn init! []
  (swap! state assoc-in [:credentials] (read-string (.getItem (.-localStorage js/window) "credentials")))
  (accountant/configure-navigation!
   {:nav-handler
    (fn [path]
      (secretary/dispatch! path))
    :path-exists?
    (fn [path]
      (secretary/locate-route path))})
  (accountant/dispatch-current!)
  (mount-root))


(comment

  (init!)

  )
