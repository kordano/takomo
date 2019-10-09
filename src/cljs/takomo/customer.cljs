(ns takomo.customer
  (:require [ajax.core :refer [GET PUT DELETE POST]]
            [takomo.components :refer [field]]
            [takomo.pages.templates :refer [creation-template]]
            [accountant.core :as acc]
            [cljs.reader :refer [read-string]]))

(defn new-customer-page [state]
  [creation-template
   state
   {:name [:text "Name" "e.g. Umbrella Corp"]
    :contact [:text "Contact" "e.g. Wesker"]
    :department [:text "Department" "e.g. R&D"]
    :city [:text "City" "e.g. Racoon City"]
    :street [:text "Street" "e.g. Main Avenue 42"]
    :postal [:text "Postal" "e.g. 3210"]
    :country [:text "Country" "e.g. USA"]}
   "customer"])

(defn customers-page [state]
  (letfn [(get-customers [] (GET "http://localhost:3000/api/customers"
                                 {:handler #(swap! state assoc :customers (read-string (str %)))
                                  :response-format :json
                                  :headers {"Authorization" (str "Token " (-> @state :credentials :token))}
                                  :keywords? true}))]
    (get-customers)
    (fn []
      [:div.container
       [:table.table
        [:thead
         [:tr [:th "ID"] [:th "Name"] [:th "Contact"] [:th ""]]]
        [:tbody
         (map (fn [{:keys [id name contact] :as customer}]
                [:tr {:key id}
                 [:td [:a {:on-click (fn []
                                       (swap! state assoc-in [:selected-customer] customer)
                                       (swap! state assoc-in [:inputs :selected-customer] customer)
                                       (acc/navigate! "/customer"))} id]]
                 [:td name]
                 [:td contact]
                 [:td [:a.delete
                       {:on-click
                        (fn []
                          ;; show confirmation dialog
                          (DELETE (str "http://localhost:3000/api/customers/" id)
                            {:handler (fn []
                                        (swap! state update-in [:notifications] conj "Customer deleted!")
                                        (get-customers))
                             :error-handler #(js/alert (read-string (str %)))
                             :headers {"Authorization" (str "Token " (-> @state :credentials :token))}
                             :keywords? true}))}]]])
              (:customers @state))]]])))

(defn customer-page [state]
  (let [{:keys [id name] :as selected-customer} (-> @state :selected-customer)
        input (-> @state :inputs :selected-customer)]
    [:div.container
     [:h1.title name]
     [:p.subtitle (str "#" id)]
     [field state :selected-customer :text :name "Name" "e.g. Umbrella Corp"]
     [field state :selected-customer :text :contact "Contact" "e.g. Wesker"]
     [field state :selected-customer :text :department "Department" "e.g. R&D"]
     [field state :selected-customer :text :city "City" "e.g. Racoon City"]
     [field state :selected-customer :text :street "Street" "e.g. Main Avenue 42"]
     [field state :selected-customer :text :postal "Postal" "e.g. 3210"]
     [field state :selected-customer :text :country "Country" "e.g. USA"]
     [:a.button.is-primary
      {:disabled (= input selected-customer)
       :on-click
       (fn []
         (PUT (str "http://localhost:3000/api/customers/" (-> @state :selected-customer :id))
           {:handler (fn []
                       (swap! state update-in [:notifications] conj "Customer updated!")
                       (swap! state assoc-in [:selected-customer] input))
            :error-handler #(js/alert (read-string (str %)))
            :response-format :json
            :format :json
            :headers {"Authorization" (str "Token " (-> @state :credentials :token))}
            :params (dissoc input :id)
            :keywords? true}))}
      "Save"]]))