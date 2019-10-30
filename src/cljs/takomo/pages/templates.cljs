(ns takomo.pages.templates
  (:require [reagent.core :as r]
            [takomo.network :as net]
            [takomo.components :refer [field select multi-select]]
            [accountant.core :as acc]
            [cljs.reader :refer [read-string]]))

(defn creation-template [state model input-keys]
  (let [inputs (r/atom {})
        capitalized (clojure.string/capitalize model)]
    [:div.container
     [:h1.title (str "Create new " capitalized)]
     (map 
      (fn [[k {:keys [input-type label placeholder allowed-values]}]]
        (case input-type
          :select [select inputs k label allowed-values]
          :multi-select [multi-select inputs k label allowed-values]
          [field inputs input-type k label placeholder]))
      input-keys)
     [:a.button.is-primary
      {:on-click
       #_#(js/alert @inputs)
       (fn []
         (net/api-post
          state 
          (str model "s") 
          @inputs 
          (fn []
            (swap! state update-in [:notifications] conj (str capitalized  " created!"))
            (reset! inputs nil))))}
      "Create"]]))


(defn overview-template [state model table-data]
  (let [plural (str model "s")
        capitalized (clojure.string/capitalize model)]
    (letfn [(get-overview [] 
              (net/api-get state 
                           plural
                           {}
                           (fn [resp] (swap! state assoc (keyword plural) (read-string (str resp))))))]
      (get-overview)
      (fn [state]
          [:div.container
           [:table.table
            [:thead
             [:tr [:th "ID"] (map (fn [[k v]] [:th {:key v} v]) table-data) [:th ""]]]
            [:tbody
             (map (fn [{:keys [id] :as el}]
                    [:tr {:key id}
                     [:td [:a {:on-click (fn []
                                           (swap! state assoc-in [:selected] el)
                                           (acc/navigate! (str "/" model)))} id]]
                     (map (fn [[k v]] [:td {:key v} v]) (select-keys el (keys table-data)))
                     [:td [:a.delete
                           {:on-click
                            (fn []
                            ;; show confirmation dialog
                              (net/api-delete
                               state
                               (str plural "/" id)
                               (fn []
                                 (swap! state update-in [:notifications] conj (str capitalized " deleted!"))
                                 (get-overview))))}]]])
                  (get @state (keyword plural)))]]]))))

(defn details-template [state model input-keys]
  (let [inputs (r/atom (:selected @state))
        plural (str model "s")
        capitalized (clojure.string/capitalize model)]
    (fn []
      (let [{:keys [id] :as selected} (:selected @state)]
        [:div.container
         [:h1.title (str capitalized " #" id)]
         (map
          (fn [[k [input-type label placeholder]]]
            [field inputs input-type k label placeholder])
          input-keys)
         [:a.button.is-primary
          {:disabled (= @inputs selected)
           :on-click
           (fn []
             (net/api-put
              state
              (str plural "/" id)
              (dissoc @inputs :id)
              (fn []
                (swap! state update-in [:notifications] conj (str capitalized " updated!"))
                (swap! state assoc-in [:selected] @inputs))))}
          "Save"]]))))
