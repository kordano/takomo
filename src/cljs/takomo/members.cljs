(ns takomo.members
  (:require [ajax.core :refer [GET PUT DELETE]]
            [takomo.components :refer [field]]
            [accountant.core :as acc]
            [cljs.reader :refer [read-string]]))

(defn get-headers [state]
    (let [token (-> @state :credentials :token)]
      {"Authorization" (str "Token " token)}))

(defn members-page [state]
  (letfn [(get-members [] (GET "http://localhost:3000/api/members"
                            {:handler #(swap! state assoc :members (read-string (str %)))
                             :response-format :json
                             :headers (get-headers state)
                             :keywords? true}))]
    (get-members)
    (fn []
      [:div.container
       [:table.table
        [:thead
         [:tr [:th "ID"] [:th "Firstname"] [:th "Lastname"] [:th "Email"] [:th ""]]]
        [:tbody
         (map (fn [{:keys [id email firstname lastname] :as member}]
                [:tr
                 [:td [:a {:on-click (fn []
                                       (swap! state assoc-in [:selected-member] member)
                                       (swap! state assoc-in [:inputs :selected-member] member)
                                       (acc/navigate! "/member"))} id]]
                 [:td firstname]
                 [:td lastname]
                 [:td email]
                 [:td [:a.delete
                       {:on-click
                        (fn []
                          ;; show confirmation dialog
                          (DELETE (str "http://localhost:3000/api/members/" id)
                            {:handler (fn []
                                        (swap! state update-in [:notifications] conj "Member deleted!")
                                        (get-members))
                             :error-handler #(js/alert (read-string (str %)))
                             :response-format :json
                             :headers {"Authorization" (str "Token " (-> @state :credentials :token))}
                             :keywords? true}))}]]])
              (:members @state))]]])))

(defn member-page [state]
  (let [{:keys [id firstname lastname] :as selected-member} (-> @state :selected-member)
        input (-> @state :inputs :selected-member)]
    [:div.container
     [:h1.title (str firstname " " lastname)]
     [:p.subtitle (str "#" id)]
     [field state :selected-member :text :firstname "Firstname" "e.g. Alice"]
     [field state :selected-member :text :lastname "Lastname" "e.g. Abernathy"]
     [field state :selected-member :email :email "Email" "e.g. alice@umbrella.corp"]
     [field state :selected-member :password :password "Password" "e.g. alicerocks!1"]
     [:a.button.is-primary
      {:disabled (and (not (:password input)) (= (dissoc input :password) selected-member))
       :on-click
       (fn []
         (PUT (str "http://localhost:3000/api/members/" (-> @state :selected-member :id))
             {:handler (fn []
                         (swap! state update-in [:notifications] conj "Member updated!")
                         (swap! state assoc-in [:inputs :selected-member :password] nil)
                         (swap! state assoc-in [:selected-member] (dissoc input :password)))
              :error-handler #(js/alert (read-string (str %)))
              :response-format :json
              :format :json
              :headers {"Authorization" (str "Token " (-> @state :credentials :token))}
              :params  (if (:password input)
                         input
                         (dissoc input :password))
              :keywords? true}))}
      "Save"]]))
