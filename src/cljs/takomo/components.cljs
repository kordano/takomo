(ns takomo.components)

(defn field [state input-type input-name label placeholder]
  [:div.field {:key input-name}
   [:label.label label]
   [:div.control
    [:input.input
     {:type input-type
      :autoComplete "new-password"
      :placeholder placeholder
      :on-change (fn [e] (swap! state assoc-in [input-name] (let [val (.. e -target -value)]
                                                              (case input-type
                                                                :number (int val)
                                                                val))))
      :value (get @state input-name)}]]])

(defn select [state input-name label allowed-values]
  [:div.field {:key input-name}
   [:label.label label]
   [:div.select
    [:select
     [:option "Please select"]
     (map
      (fn [{:keys [id label]}]
        [:option {:value id
                  :on-click (fn [e]
                             (swap! state assoc input-name id))} label])
      allowed-values)]]])


(defn multi-select [state input-name label allowed-values]
 [:div.field {:key input-name}
  [:label.label label]
  [:div.select.is-multiple
   [:select {:multiple true :size 4}
    (map
     (fn [{:keys [id label]}]
       [:option {:value id 
                 :on-click (fn [e]
                             (swap! state update input-name 
                                    (fn [old] (if old
                                                (conj old id)
                                                [id]))))} label])
     allowed-values)]]])
