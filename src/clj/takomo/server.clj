(ns takomo.server
  (:require [reitit.ring :as ring]
            [muuntaja.core :as m]
            [reitit.swagger :as swagger]
            [reitit.swagger-ui :as swagger-ui]
            [clj-time.core :as time]
            [reitit.coercion.spec]
            [reitit.ring.coercion :as coercion]
            [reitit.ring.middleware.parameters :as parameters]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.interceptor.sieppari :as sieppari]
            [ring.middleware.cors :refer [wrap-cors]]
            [clojure.spec.alpha :as s]
            [buddy.sign.jwt :as jwt]
            [buddy.core.nonce :as nonce]
            [buddy.auth :refer [authenticated? throw-unauthorized]]
            [buddy.auth.backends.token :refer [jwe-backend]]
            [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]
            [mount.core :refer [defstate]]
            [takomo.model]
            [takomo.store
             [customer :as sc]
             [document :as sd]
             [effort :as se]
             [member :as sm]
             [task :as st]
             [prjct :as sp]]
            [org.httpkit.server :as kit]))

(def secret (nonce/random-bytes 32))

(s/def ::path-params (s/keys :req-un [:takomo.model/id]))

(defn ok [d] {:status 200 :body d})
(defn bad-request [d] {:status 400 :body d})
(defn body [req] (get-in req [:parameters :body]))
(defn path [req k] (get-in req [:parameters :path k]))

(def auth-backend (jwe-backend {:secret secret
                                :options {:alg :a256kw :enc :a128gcm}}))

(def non-auth-uris #{"/swagger.json" "/api/login"})

(defn wrap-auth [handler]
  (fn [{:keys [uri] :as req}]
    (if (contains? non-auth-uris uri)
      (handler req)
      (if-not (authenticated? req)
        (throw-unauthorized)
        (handler req)))))

(def app
  (->
   (ring/ring-handler
    (ring/router
     [["/swagger.json"
       {:get {:no-doc true
              :swagger {:info {:title "Takomo API"
                               :description "Part of CARD stack"}
                        :securityDefinitions {:JWT {:type "apiKey" :name "Authorization" :in "header"}}}
              :handler (swagger/create-swagger-handler)}}]

      ["/api"

       ["/login"
        {:post {:responses {200 {:body :takomo.model/jws}}
                :parameters {:body :takomo.model/credentials}
                :swagger {:tags ["self-service"]}
                :handler (fn [req]
                           (let [username (-> req body :username)
                                 password (-> req body :password)
                                 valid? (sm/credentials-valid? username password)]
                             (if valid?
                               (let [claims {:user (keyword username)
                                             :exp (time/plus (time/now) (time/seconds 3600))}
                                     token (jwt/encrypt claims secret {:alg :a256kw :enc :a128gcm})]
                                 (ok {:token token}))
                               (bad-request {:message "Invalid credentials."}))))}}]

       ["/members" {:get  {:responses {200 {:body :takomo.model/members}}
                           :swagger {:tags ["member"]}
                           :handler   (fn [req] (ok (sm/read-members)))}

                    :post {:parameters {:body :takomo.model/new-member}
                           :swagger {:tags ["member"]}
                           :handler    (fn [req]
                                         (-> req body sm/create-member)
                                         (ok {}))}}]

       ["/members/:id"
        {:put    {:parameters {:body :takomo.model/new-member
                               :path ::path-params}

                  :swagger {:tags ["member"]}
                  :handler    (fn [req]
                                (-> (body req)
                                    (assoc :db/id (path req :id))
                                    sm/update-member)
                                (ok {}))}

         :delete {:parameters {:path ::path-params}
                  :swagger {:tags ["member"]}
                  :handler    (fn [req]
                                (sm/delete-member (path req :id))
                                (ok {}))}}]

       ["/customers" {:get {:responses {200 {:body :takomo.model/customers}}
                            :swagger {:tags ["customer"]}
                            :handler (fn [req] (ok (sc/read-customers)))}
                      :post {:parameters {:body :takomo.model/new-customer}
                             :swagger {:tags ["customer"]}
                             :handler (fn [req] (sc/create-customer (body req))
                                        (ok {}))}}]

       ["/customers/:id"
        {:put    {:parameters {:body :takomo.model/new-customer
                               :path ::path-params}

                  :swagger {:tags ["customer"]}
                  :handler    (fn [{{updated-customer :body {:keys [id]} :path} :parameters}]
                                (sc/update-customer (assoc updated-customer :db/id id))
                                (ok {}))}
         :delete {:parameters {:path ::path-params}
                  :swagger {:tags ["customer"]}
                  :handler    (fn [{{{:keys [id]} :path} :parameters}]
                                (sc/delete-customer id)
                                (ok {}))}}]

       ["/documents" {:get {:responses {200 {:body :takomo.model/documents}}
                            :swagger {:tags ["document"]}
                            :handler (fn [req]
                                       {:status 200
                                        :body (sd/read-documents)})}}]
       ["/documents/:id"
        {:delete {:parameters {:path ::path-params}
                  :swagger {:tags ["document"]}
                  :handler    (fn [{{{:keys [id]} :path} :parameters}]
                                (sd/delete-document id)
                                (ok {}))}}]

       ["/tasks" {:get {:responses {200 {:body :takomo.model/tasks}}
                        :swagger {:tags ["task"]}
                        :handler (fn [req]
                                   {:status 200
                                    :body (st/read-tasks)})}
                  :post {:parameters {:body :takomo.model/new-task}
                         :swagger {:tags ["task"]}
                         :handler (fn [req] (st/create-task (body req))
                                    (ok {}))}}]
       ["/tasks/:id"
        {:put    {:parameters {:body :takomo.model/task
                               :path ::path-params}

                  :swagger {:tags ["task"]}
                  :handler    (fn [{{updated-task :body {:keys [id]} :path} :parameters}]
                                (st/update-task (assoc updated-task :db/id id))
                                (ok {}))}
         :delete {:parameters {:path ::path-params}
                  :swagger {:tags ["task"]}
                  :handler    (fn [{{{:keys [id]} :path} :parameters}]
                                (st/delete-task id)
                                (ok {}))}}]
       ["/efforts" {:get {:responses {200 {:body :takomo.model/efforts}}
                          :swagger {:tags ["effort"]}
                          :handler (fn [req]
                                     {:status 200
                                      :body (se/read-efforts)})}
                    :post {:parameters {:body :takomo.model/new-effort}
                           :swagger {:tags ["effort"]}
                           :handler (fn [{{new-effort :body} :parameters}]
                                      (se/create-effort new-effort)
                                      (ok {}))}}]
       ["/efforts/:id"
        {:put    {:parameters {:body :takomo.model/new-effort
                               :path ::path-params}

                  :swagger {:tags ["effort"]}
                  :handler    (fn [{{updated-effort :body {:keys [id]} :path} :parameters}]
                                (se/update-effort (assoc updated-effort :db/id id))
                                (ok {}))}
         :delete {:parameters {:path ::path-params}
                  :swagger {:tags ["effort"]}
                  :handler    (fn [{{{:keys [id]} :path} :parameters}]
                                (se/delete-effort id)
                                (ok {}))}}]

       ["/projects" {:get {:responses {200 {:body :takomo.model/projects}}
                           :swagger {:tags ["project"]}
                           :handler (fn [req]
                                      {:status 200
                                       :body (sp/read-projects)})}
                     :post {:parameters {:body :takomo.model/new-project}
                            :swagger {:tags ["project"]}
                            :handler (fn [{{new-project :body} :parameters}]
                                       (sp/create-project new-project)
                                       (ok {}))}}]
       ["/projects/:id"
        {:put    {:parameters {:body :takomo.model/new-project
                               :path ::path-params}

                  :swagger {:tags ["project"]}
                  :handler    (fn [{{updated-project :body {:keys [id]} :path} :parameters}]
                                (sp/update-project (assoc updated-project :db/id id))
                                (ok {}))}
         :delete {:parameters {:path ::path-params}
                  :swagger {:tags ["project"]}
                  :handler    (fn [{{{:keys [id]} :path} :parameters}]
                                (sp/delete-project id)
                                (ok {}))}}]]]

     {:data {:coercion reitit.coercion.spec/coercion
             :muuntaja m/instance
             :middleware [swagger/swagger-feature
                          #(wrap-authorization % auth-backend)
                          #(wrap-authentication % auth-backend)
                          parameters/parameters-middleware
                          muuntaja/format-negotiate-middleware
                          muuntaja/format-response-middleware
                          muuntaja/format-request-middleware
                          coercion/coerce-response-middleware
                          coercion/coerce-request-middleware
                          wrap-auth]}})
    (ring/routes
     (swagger-ui/create-swagger-ui-handler
      {:path "/"
       :config {:validatorUrl nil :operationsSorter "alpha"}})
     (ring/create-default-handler))
    {:executor sieppari/executor})
   (wrap-cors :access-control-allow-origin [#"http://localhost" #"http://localhost:8080"]
              :access-control-allow-methods [:get :put :post :delete])))

(defstate server
  :start (kit/run-server app {:port 3000})
  :stop (server))
