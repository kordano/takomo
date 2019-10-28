(ns takomo.components)

(defn field [state input-type input-name label placeholder]
  [:div.field {:key input-name}
   [:label.label label]
   [:div.control
    [:input.input
     {:type input-type
      :autoComplete "new-password"
      :placeholder placeholder
      :on-change (fn [e] (swap! state assoc-in [input-name] (.. e -target -value)))
      :value (get @state input-name)}]]])

(defn select [state input-name label placeholder allowed-values]
  [:div.field {:key input-name}
   [:label.label label]
   [:div.select
    [:select
     (map
      (fn [allowed-value]
        [:option {:on-click (fn [e] (swap! state assoc input-name allowed-value))} allowed-value])
      allowed-values)]]])

(defn id-select [state input-name label placeholder allowed-values]
  [:div.field {:key input-name}
   [:label.label label]
   [:div.select
    [:select
     (map
      (fn [[id allowed-value]]
        [:option {:on-click (fn [e] (swap! state assoc input-name id))} allowed-value])
      allowed-values)]]])
