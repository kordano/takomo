(ns takomo.store
  (:require [clojure.spec.alpha :as s]
            [datahike.api :as d]
            [hasch.core :as h]))

(defonce state (atom {:uri  "datahike:mem://takomo"
                      :conn nil}))

(defn init []
  (let [uri (:uri @state)
        schema-tx (-> "resources/dh/schema.edn" slurp read-string)]
    (d/create-database uri :initial-tx schema-tx)
    (swap! state assoc :conn (d/connect uri))))

(defn get-db []
  (-> state deref :conn deref))

(defn get-conn []
  (:conn @state))


(comment

  (init)

  (get-db)

  (d/delete-database (:uri @state))

  (takomo.store.customer/create-customer {:name "Foo"})

  (let [{:keys [foo/bar]} {:foo/bar 2}]
    bar))
