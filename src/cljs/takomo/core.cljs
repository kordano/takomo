(ns takomo.core
  (:require [reagent.core :as r]
            ["@material-ui/core" :refer [Button]]))

(def click-count (r/atom 0))

(defn simple-component []
  [:div
   [:p "The state has a value: " @click-count]
   [:> Button {:variant "contained" :color "primary" :on-click #(swap! click-count inc)} "Click!"]])


(defn init []
  (r/render
    [simple-component]
    (js/document.getElementById "root")))


(comment

  @click-count

  (r/render
    [simple-component]
    (js/document.getElementById "root"))

  )
