(ns takomo.components.dialog
  (:require
   [reagent.core :as r]
   [ajax.core :refer [POST]]
   ["@material-ui/core" :refer [Dialog DialogActions DialogContent DialogContentText DialogTitle Button TextField]]))

(defn member-text-field [state attribute type label]
  [:> TextField {:autoFocus true
                 :fullWidth true
                 :label label
                 :value (or (get-in @state [:dialogs :member :input attribute]) "")
                 :onChange #(swap! state assoc-in [:dialogs :member :input attribute] (.-value (.-target %)))
                 :type type}])

(defn member-dialog [state]
  (letfn [(close-dialog [] (swap! state assoc-in [:dialogs :member :open?] false))]
    (let [open? (get-in @state [:dialogs :member :open?])]
      [:> Dialog {:open open?
                  :onClose close-dialog}
       [:> DialogTitle "Create Member"]
       [:> DialogContent
        (member-text-field state :member/firstname :text "First name")
        (member-text-field state :member/lastname :text "Last name")
        (member-text-field state :member/email :email "Email")
        (member-text-field state :member/password :password "Password")]
       [:> DialogActions
        [:> Button {:onClick (fn []
                               (let [new-member (get-in @state [:dialogs :member :input])]
                                 (js/alert new-member)
                                 (POST "http://localhost:3000/api/members" {:params new-member
                                                                            :handler (fn [r]
                                                                                       (js/alert "RESPONSE:" (str r))
                                                                                       (close-dialog))
                                                                            :error-handler (fn [e] (js/alert (str e)))
                                                                            :format :json
                                                                            :response-format :json}))
                               ) :color :primary} "Create"]]])))

(clj->js {:foo/bar "baz"})
