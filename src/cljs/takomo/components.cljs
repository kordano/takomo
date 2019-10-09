(ns takomo.components)

(defn field [state input-type input-name label placeholder]
  [:div.field {:key input-name}
     [:label.label label]
   [:div.control
    [:input.input
     {:type input-type
      :autoComplete "new-password"
      :placeholder placeholder
      :on-change (fn [e]
                   (swap! state
                          assoc-in
                          [input-name]
                          (.-value (.-target e))))
      :value (-> @state input-name)}]]])
