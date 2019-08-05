(ns takomo.store
  (:require [clojure.spec.alpha :as s]
            [datahike.api :as d]
            [hasch.core :as h]))

(defonce state (atom {:uri  "datahike:mem://takomo"
                      :conn nil}))

(defn init []
  (let [uri (:uri @state)
        schema-tx (-> "resources/dh/schema.edn" slurp read-string)]
    (d/create-database {:uri uri :initial-tx schema-tx})
    (swap! state assoc :conn (d/connect uri))))



(defn get-db []
  (d/db (:conn @state)))

(defn get-conn []
  (:conn @state))


(comment

  (d/delete-database (:uri @state))


  (let [{:keys [foo/bar]} {:foo/bar 2}]
    bar)

  )
