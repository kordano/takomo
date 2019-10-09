(ns takomo.pages.templates
  (:require [reagent.core :as r]
            [takomo.network :as net]
            [takomo.components :refer [field]]))

(defn creation-template [state input-keys model]
  (let [inputs (r/atom {})
        capitalized (clojure.string/capitalize model)]
    [:div.container
     [:h1.title (str "Create new " capitalized)]
     (map 
      (fn [[k [input-type label placeholder]]]
        [field inputs input-type k label placeholder])
      input-keys)
     [:a.button.is-primary
      {:on-click
       (fn []
         (net/post 
            state 
            (str model "s") 
            @inputs 
            (fn []
              (swap! state update-in [:notifications] conj (str capitalized  " created!"))
              (reset! inputs nil))))}
      "Create"]]))

