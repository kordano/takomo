(ns takomo.network
  (:require [ajax.core :refer [GET PUT DELETE POST]]
            [cljs.reader :refer [read-string]]))

(def base-uri "http://localhost:3000/api/")

(defn post [state model params handler]
  (POST (str base-uri model)
        {:handler handler
         :error-handler #(js/alert (read-string (str %)))
         :response-format :json
         :format :json
         :headers {"Authorization" (str "Token " (-> @state :credentials :token))}
         :params params
         :keywords? true}))
         