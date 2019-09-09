(ns takomo.core
  (:require [reagent.core :as r]
            [ajax.core :refer [GET POST]]
            [cljs.reader :refer [read-string]]
            [takomo.components.dialog :refer [member-dialog]]
            [takomo.components.members :refer [members-component]]
            [secretary.core :as secretary :refer-macros [defroute]]
            ["@material-ui/core" :refer [Button Table Paper TableBody TableCell TableHead TableRow Typography Grid Fab]]
            ["@material-ui/icons" :refer [Add]]))

(defonce state (r/atom {}))


(def selected-page (r/atom members-component))

(defn login []
  [:div
   [:h1 "LOGIN"]])

(defn page []
  [@selected-page state])

(defroute "/" []
  (reset! selected-page login))

(defroute "/members" []
  (reset! selected-page members-component))

(defn mount-root []
  (r/render [page] (.getElementById js/document "root")))

(defn init []
  (mount-root))


(comment


  (GET "http://localhost:3000/api/members" {:handler #(swap! state assoc :members (read-string (str %)))
                                            :response-format :json
                                            :keywords? true})


  (@state :members)

  @state

  (swap! state assoc-in [:dialogs :member :open?] false)


  (init)

  )
