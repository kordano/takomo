(ns takomo.components)

(defn field [state page input-type input-name label placeholder]
  [:div.field
     [:label.label label]
   [:div.control
    [:input.input
     {:type input-type
      :autocomplete "new-password"
      :placeholder placeholder
      :on-change (fn [e]
                   (swap! state
                          assoc-in
                          [:inputs page input-name]
                          (.-value (.-target e))))
      :value (-> @state :inputs page input-name)}]]])
