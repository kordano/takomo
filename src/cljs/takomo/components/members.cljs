(ns takomo.components.members
  (:require [takomo.components.dialog :refer [member-dialog]]
            ["@material-ui/core" :refer [Button Table Paper TableBody TableCell TableHead TableRow Typography Grid Fab]]
            ["@material-ui/icons" :refer [Add]]))

(def style
  {:fab {:padding "8px 8px 8px 8px"
         :position :absolute
         :right "8px"
         :bottom "8px"}})

(defn members-table [members]
  (let []
    [:> Paper
     [:> Grid {:container true
               :direction :column}

      [:> Grid {:item true} [:> Typography {:className "title" :variant :h5} "Members"]]
      [:> Grid {:item true}
       [:> Table
        [:> TableHead
         [:> TableRow
          [:> TableCell "ID"]
          [:> TableCell "First Name"]
          [:> TableCell "Last Name"]
          [:> TableCell "Email"]]]
        [:> TableBody
         (map (fn  [{:keys [id firstname lastname email]}]
                ^{:key id} [:> TableRow
                            [:> TableCell id]
                            [:> TableCell firstname]
                            [:> TableCell lastname]
                            [:> TableCell email]])
              members)]]]]]))

(defn members-component [state]
  [:div
   (members-table (@state :members))
   (member-dialog state)
   [:> Fab {:size :medium :color :primary :style (:fab style) :onClick #(swap! state assoc-in [:dialogs :member :open?] true)} [:> Add]]])
