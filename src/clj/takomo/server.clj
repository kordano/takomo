(ns takomo.server
  (:require [reitit.http :as http]
            [reitit.ring :as ring]
            [takomo.store :as store]
            [muuntaja.core :as m]
            [reitit.coercion.spec]
            [reitit.ring.coercion :as coercion]
            [reitit.ring.middleware.parameters :as parameters]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.coercion :as rrc]
            [clojure.spec.alpha :as spec]
            [org.httpkit.server :as kit]))

(defonce state (atom {:server nil}))

(def app
  (ring/ring-handler
   (ring/router
    ["/api"
     ["/members" {:get {:responses {200 {:body [{:db/id int?
                                                 :member/firstname string?
                                                 :member/lastname string?
                                                 :member/email string?}]}}
                        :handler (fn [req]
                                   {:status 200
                                    :body (store/get-members)})}
                  :post {:parameters {:body {:firstname string?
                                             :lastname string?
                                             :email string?
                                             :password string?}}
                         :handler (fn [{{new-member :body} :parameters}]
                                    (store/create-member new-member)
                                    {:status 200})}}]
     ["/members/:id"
      {:put {:parameters {:body {:firstname string?
                                 :lastname string?
                                 :email string?
                                 :password string?}
                          :path {:id int?}}

             :handler (fn [{{updated-member :body {:keys [id]} :path} :parameters}]
                        ()
                        (store/update-member (assoc updated-member :id id))
                        {:status 200})}}]]
    {:data {:coercion reitit.coercion.spec/coercion
            :muuntaja m/instance
            :middleware [parameters/parameters-middleware
                         muuntaja/format-negotiate-middleware
                         muuntaja/format-response-middleware
                         muuntaja/format-request-middleware
                         coercion/coerce-response-middleware
                         coercion/coerce-request-middleware]}})))

(defn start-server []
  (swap! state assoc :server (kit/run-server app {:port 3000})))

(defn stop-server []
  ((:server @state))
  (swap! state assoc :server nil))


(comment

  (start-server)

  (stop-server)



  @state

  (store/init)

  (store/create-member {:firstname "alice" :lastname "bob" :email "foo" :password "123"})

  (store/get-members)

  (.start server)


  )
