(ns takomo.network
  (:require [ajax.core :refer [GET PUT DELETE POST]]
            [cljs.reader :refer [read-string]]))

(def base-uri "http://localhost:3000/api/")

(defn get-header [state]
  {"Authorization" (str "Token " (-> @state :credentials :token))})

(defn error-handler [error-response]
  (js/alert (read-string (str error-response))))

(defn api-post [state endpoint params handler]
  (POST (str base-uri endpoint)
    {:handler handler
     :error-handler #(js/alert (read-string (str %)))
     :response-format :json
     :format :json
     :headers (get-header state)
     :params params
     :keywords? true}))

(defn api-get [state endpoint params handler]
  (GET (str base-uri endpoint)
    {:handler handler
     :error-handler error-handler
     :response-format :json
     :headers (get-header state)
     :params params
     :keywords? true}))

(defn api-delete [state endpoint handler]
  (DELETE (str base-uri endpoint)
          {:handler handler
           :error-handler error-handler
           :headers (get-header state)
           :keywords? true}))

(defn api-put [state endpoint params handler]
  (PUT (str base-uri endpoint)
    {:handler handler
     :error-handler error-handler
     :response-format :json
     :format :json
     :headers (get-header state)
     :params params
     :keywords? true}))
