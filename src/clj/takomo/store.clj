(ns takomo.store
  (:require [clojure.spec.alpha :as s]
            [mount.core :refer [defstate]]
            [takomo.config :refer [config]]
            [datahike.api :as d]
            [hasch.core :as h]))

(defstate conn
  :start  (let [{:keys [datahike/uri]} config
                schema-tx (-> "resources/dh/schema.edn" slurp read-string)
                data-tx (-> "env/dev/resources/dev-data.edn" slurp read-string)]
            (when-not (d/database-exists? uri)
              (d/create-database uri :initial-tx (vec (concat schema-tx data-tx))))
            (d/connect uri))
  :stop (d/release conn))
