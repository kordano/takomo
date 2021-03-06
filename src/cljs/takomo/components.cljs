(ns takomo.components)

(defn field [state input-type input-name label placeholder]
  [:div.field {:key input-name}
   [:label.label label]
   [:div.control
    [:input.input
     {:type (case input-type
              :int :number
              :float :number
              :currency :number
              input-type)
      :lang "en"
      :step "any"
      :autoComplete "new-password"
      :placeholder placeholder
      :on-change (fn [e] (swap! state assoc-in [input-name] (let [val (.. e -target -value)]
                                                              (case input-type
                                                                :number (js/parseFloat val)
                                                                :int (js/parseInt val)
                                                                :float (js/parseFloat val)
                                                                :currency (js/parseInt val)
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
                  :selected (= (get @state input-name) id)
                  :on-click (fn [e]
                             (swap! state assoc input-name id))} label])
      allowed-values)]]])

(defn multi-select [state input-name label allowed-values]
  (let [selected-values (into #{} (get @state input-name))]
    [:div.field {:key input-name}
     [:label.label label]
     [:div.select.is-multiple
      [:select {:multiple true :size 4}
       (map
        (fn [{:keys [id label]}]
          [:option {:value id
                    :selected (contains? selected-values id)
                    :on-click (fn []
                                (swap! state update input-name
                                       (fn [old] (if old
                                                   (conj old id)
                                                   [id]))))} label])
        allowed-values)]]]))
