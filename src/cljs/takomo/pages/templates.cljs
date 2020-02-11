(ns takomo.pages.templates
  (:require [reagent.core :as r]
            [takomo.network :as net]
            [takomo.components :refer [field select multi-select]]
            [cljs.reader :refer [read-string]]))

(defn overview-template [state model table-data input-keys]
  (let [plural (case model
                 "company" "companies"
                 (str model "s"))
        inputs (r/atom {})
        edit-inputs (r/atom (:selected @state))
        toggles (r/atom {:creation-modal false
                         :edit-modal false})
        capitalized (clojure.string/capitalize model)]
    (letfn [(get-overview []
              (net/api-get state
                           plural
                           {}
                           (fn [resp] (swap! state assoc (keyword plural) (read-string (str resp))))))]
      (get-overview)
      (fn [state]
        [:div.container
         [:div.columns.level
          [:div.column.level-left
           [:h1.title (clojure.string/capitalize plural)]]
          [:dib.column.level-right
           [:button.button.is-primary
            {:on-click (fn [] (swap! toggles assoc :creation-modal true))}
            (str "Add new " capitalized)]]]
         [:table.table
          [:thead
           [:tr (map (fn [[k v]] [:th {:key v} v]) table-data) [:th ""]]]
          [:tbody
           (map (fn [{:keys [id] :as el}]
                  [:tr {:key id}
                   (map
                    (fn [[k v]] [:td {:key (or (str (get el k) k) (str id k))} (or (get el k) "-")])
                    table-data)
                   [:td
                    [:div
                     [:a
                      {:on-click (fn []
                                   (reset! edit-inputs el)
                                   (swap! toggles assoc :edit-modal true))}
                      [:span.icon [:i.fas.fa-edit.has-text-dark]]]
                     [:a
                      {:on-click (fn []
                                   (net/api-delete
                                    state
                                    (str plural "/" id)
                                    (fn []
                                      (swap! state update-in [:notifications] conj (str capitalized " deleted!"))
                                      (get-overview))))}
                      [:span.icon [:i.fas.fa-trash.has-text-danger]]]]]])
                (get @state (keyword plural)))]]
         [:div.modal
          {:class (if (:creation-modal @toggles) "is-active is-clipped" "")}
          [:div.modal-background]
          [:div.modal-content
           [:div.box
            [:div.container
             [:h1.title (str "Add new " capitalized)]
             (map
              (fn [[k {:keys [input-type label placeholder allowed-values]}]]
                (case input-type
                  :select [select inputs k label allowed-values]
                  :multi-select [multi-select inputs k label allowed-values]
                  [field inputs input-type k label placeholder]))
              input-keys)
             [:a.button.is-primary
              {:on-click
               (fn []
                 (let [data @inputs]
                   (net/api-post
                    state
                    (str model "s")
                    data
                    (fn []
                      (swap! toggles assoc :creation-modal false)
                      (swap! state update-in [:notifications] conj (str capitalized  " created!"))
                      (reset! inputs nil)
                      (get-overview)))))}
              "Create"]]]]
          [:button.modal-close.is-large {:aria-label :close :on-click #(swap! toggles assoc :creation-modal false)}]]
         [:div.modal
          {:class (if (:edit-modal @toggles) "is-active is-clipped" "")}
          [:div.modal-background]
          [:div.modal-content
           [:div.box
            (let [{:keys [id] :as selected} (:selected @state)]
              [:div.container
               [:h1.title (str capitalized " #" id)]
               (map
                (fn [[k {:keys [input-type label placeholder allowed-values]}]]
                  (case input-type
                    :select [select edit-inputs k label allowed-values]
                    :multi-select [multi-select edit-inputs k label allowed-values]
                    [field edit-inputs input-type k label placeholder]))
                input-keys)
               [:a.button.is-primary
                {:disabled (= @edit-inputs selected)
                 :on-click
                 (fn []
                   (net/api-put
                    state
                    (str plural "/" id)
                    (dissoc @edit-inputs :id)
                    (fn []
                      (swap! toggles assoc :edit-modal false)
                      (swap! state update-in [:notifications] conj (str capitalized " updated!"))
                      (swap! state assoc-in [:selected] @edit-inputs))))}
                "Save"]])]]
          [:button.modal-close.is-large {:aria-label :close :on-click #(swap! toggles assoc :edit-modal false)}]]
         ]))))
