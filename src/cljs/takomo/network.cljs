(ns takomo.network
  (:require [ajax.core :refer [GET PUT DELETE POST]]
            [takomo.util :refer [logout]]
            [cljs.reader :refer [read-string]]))

(def base-uri "http://localhost:3000/api/")

(defn get-header [state]
  (let [{:keys [token expired]} (:credentials @state)
        now (js/Date.)]
    (if (< expired now)
      (do
        (logout state)
        nil)
      {"Authorization" (str "Token " token)})))

(defn error-handler [error-response]
  (js/alert (read-string (str error-response))))

(defn api-post [state endpoint params handler]
  (if-let [header (get-header state)]
    (POST (str base-uri endpoint)
        {:handler handler
         :error-handler #(js/alert (read-string (str %)))
         :response-format :json
         :format :json
         :headers header
         :params params
         :keywords? true})
    (js/alert "Logged out!")))

(defn api-get [state endpoint params handler]
  (if-let [header (get-header state)]
    (GET (str base-uri endpoint)
        {:handler handler
         :error-handler error-handler
         :response-format :json
         :headers header
         :params params
         :keywords? true})
    (js/alert "Logged out!")))

(defn api-delete [state endpoint handler]
  (if-let [header (get-header state)]
    (DELETE (str base-uri endpoint)
        {:handler handler
         :error-handler error-handler
         :headers header
         :keywords? true})
    (js/alert "Logged out!")))

(defn api-put [state endpoint params handler]
  (if-let [header (get-header state)]
    (PUT (str base-uri endpoint)
        {:handler handler
         :error-handler error-handler
         :response-format :json
         :format :json
         :headers header
         :params params
         :keywords? true})
    (js/alert "Logged out!")))
