(ns takomo.server
  (:require [reitit.http :as http]
            [reitit.ring :as ring]
            [muuntaja.core :as m]
            [reitit.swagger :as swagger]
            [reitit.swagger-ui :as swagger-ui]
            [reitit.coercion.spec]
            [reitit.ring.coercion :as coercion]
            [reitit.ring.middleware.parameters :as parameters]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.interceptor.sieppari :as sieppari]
            [reitit.ring.coercion :as rrc]
            [ring.middleware.cors :refer [wrap-cors]]
            [clojure.spec.alpha :as s]
            [takomo.model]
            [takomo.store
             [customer :as sc]
             [document :as sd]
             [effort :as se]
             [member :as sm]
             [task :as st]
             [prjct :as sp]]
            [org.httpkit.server :as kit]))

(defonce state (atom {:server nil}))

(s/def ::path-params (s/keys :req-un [:takomo.model/id]))

(def app
  (->
   (ring/ring-handler
    (ring/router
     [["/swagger.json"
       {:get {:no-doc true
              :swagger {:info {:title "Takomo API"
                               :description "Part of CARD stack"}}
              :handler (swagger/create-swagger-handler)}}]

      ["/api"

       ["/members" {:get  {:responses {200 {:body :takomo.model/members}}
                           :swagger {:tags ["member"]}
                           :handler   (fn [req]
                                        {:status 200
                                         :body   (sm/read-members)})}
                    :post {:parameters {:body :takomo.model/member}
                           :swagger {:tags ["member"]}
                           :handler    (fn [{{new-member :body} :parameters}]
                                         (sm/create-member new-member)
                                         {:status 200})}}]

       ["/members/:id"
        {:put    {:parameters {:body :takomo.model/member
                               :path ::path-params}

                  :swagger {:tags ["member"]}
                  :handler    (fn [{{updated-member :body {:keys [id]} :path} :parameters}]
                                (sm/update-member (assoc updated-member :db/id id))
                                {:status 200})}
         :delete {:parameters {:path ::path-params}
                  :swagger {:tags ["member"]}
                  :handler    (fn [{{{:keys [id]} :path} :parameters}]
                                (sm/delete-member id)
                                {:status 200})}}]

       ["/customers" {:get {:responses {200 {:body :takomo.model/customers}}
                            :swagger {:tags ["customer"]}
                            :handler (fn [req]
                                       {:status 200
                                        :body (sc/read-customers)})}
                      :post {:parameters {:body :takomo.model/customer}
                             :swagger {:tags ["customer"]}
                             :handler (fn [{{new-customer :body} :parameters}]
                                        (sc/create-customer new-customer)
                                        {:status 200})}}]

       ["/customers/:id"
        {:put    {:parameters {:body :takomo.model/customer
                               :path ::path-params}

                  :swagger {:tags ["customer"]}
                  :handler    (fn [{{updated-customer :body {:keys [id]} :path} :parameters}]
                                (sc/update-customer (assoc updated-customer :db/id id))
                                {:status 200})}
         :delete {:parameters {:path ::path-params}
                  :swagger {:tags ["customer"]}
                  :handler    (fn [{{{:keys [id]} :path} :parameters}]
                                (sc/delete-customer id)
                                {:status 200})}}]

       ["/documents" {:get {:responses {200 {:body :takomo.model/documents}}
                            :swagger {:tags ["document"]}
                            :handler (fn [req]
                                       {:status 200
                                        :body (sd/read-documents)})}
                      :post {:parameters {:body :takomo.model/document}
                             :swagger {:tags ["document"]}
                             :handler (fn [{{new-document :body} :parameters}]
                                        (sd/create-document new-document)
                                        {:status 200})}}]
       ["/documents/:id"
        {:put    {:parameters {:body :takomo.model/document
                               :path ::path-params}

                  :swagger {:tags ["document"]}
                  :handler    (fn [{{updated-document :body {:keys [id]} :path} :parameters}]
                                (sd/update-document (assoc updated-document :db/id id))
                                {:status 200})}
         :delete {:parameters {:path ::path-params}
                  :swagger {:tags ["document"]}
                  :handler    (fn [{{{:keys [id]} :path} :parameters}]
                                (sd/delete-document id)
                                {:status 200})}}]

       ["/tasks" {:get {:responses {200 {:body :takomo.model/tasks}}
                        :swagger {:tags ["task"]}
                        :handler (fn [req]
                                   {:status 200
                                    :body (st/read-tasks)})}
                  :post {:parameters {:body :takomo.model/task}
                         :swagger {:tags ["task"]}
                         :handler (fn [{{new-task :body} :parameters}]
                                    (st/create-task new-task)
                                    {:status 200})}}]
       ["/tasks/:id"
        {:put    {:parameters {:body :takomo.model/task
                               :path ::path-params}

                  :swagger {:tags ["task"]}
                  :handler    (fn [{{updated-task :body {:keys [id]} :path} :parameters}]
                                (st/update-task (assoc updated-task :db/id id))
                                {:status 200})}
         :delete {:parameters {:path ::path-params}
                  :swagger {:tags ["task"]}
                  :handler    (fn [{{{:keys [id]} :path} :parameters}]
                                (st/delete-task id)
                                {:status 200})}}]
       ["/efforts" {:get {:responses {200 {:body :takomo.model/efforts}}
                          :swagger {:tags ["effort"]}
                          :handler (fn [req]
                                     {:status 200
                                      :body (se/read-efforts)})}
                    :post {:parameters {:body :takomo.model/effort}
                           :swagger {:tags ["effort"]}
                           :handler (fn [{{new-effort :body} :parameters}]
                                      (se/create-effort new-effort)
                                      {:status 200})}}]
       ["/efforts/:id"
        {:put    {:parameters {:body :takomo.model/effort
                               :path ::path-params}

                  :swagger {:tags ["effort"]}
                  :handler    (fn [{{updated-effort :body {:keys [id]} :path} :parameters}]
                                (se/update-effort (assoc updated-effort :db/id id))
                                {:status 200})}
         :delete {:parameters {:path ::path-params}
                  :swagger {:tags ["effort"]}
                  :handler    (fn [{{{:keys [id]} :path} :parameters}]
                                (se/delete-effort id)
                                {:status 200})}}]

       ["/projects" {:get {:responses {200 {:body :takomo.model/projects}}
                           :swagger {:tags ["project"]}
                           :handler (fn [req]
                                      {:status 200
                                       :body (sp/read-projects)})}
                     :post {:parameters {:body :takomo.model/project}
                            :swagger {:tags ["project"]}
                            :handler (fn [{{new-project :body} :parameters}]
                                       (sp/create-project new-project)
                                       {:status 200})}}]
       ["/projects/:id"
        {:put    {:parameters {:body :takomo.model/project
                               :path ::path-params}

                  :swagger {:tags ["project"]}
                  :handler    (fn [{{updated-project :body {:keys [id]} :path} :parameters}]
                                (sp/update-project (assoc updated-project :db/id id))
                                {:status 200})}
         :delete {:parameters {:path ::path-params}
                  :swagger {:tags ["project"]}
                  :handler    (fn [{{{:keys [id]} :path} :parameters}]
                                (sp/delete-project id)
                                {:status 200})}}]

       ]]


     {:data {:coercion reitit.coercion.spec/coercion
             :muuntaja m/instance
             :middleware [swagger/swagger-feature
                          parameters/parameters-middleware
                          muuntaja/format-negotiate-middleware
                          muuntaja/format-response-middleware
                          muuntaja/format-request-middleware
                          coercion/coerce-response-middleware
                          coercion/coerce-request-middleware]}})
    (ring/routes
     (swagger-ui/create-swagger-ui-handler
      {:path "/"
       :config {:validatorUrl nil :operationsSorter "alpha"}})
     (ring/create-default-handler))
    {:executor sieppari/executor})
   (wrap-cors :access-control-allow-origin [#"http://localhost" #"http://localhost:8080"]
              :access-control-allow-methods [:get :put :post :delete])))

(defn start-server []
  (swap! state assoc :server (kit/run-server app {:port 3000})))

(defn stop-server []
  ((:server @state))
  (swap! state assoc :server nil))


(comment

  (start-server)

  (stop-server)


  )
