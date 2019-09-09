(ns takomo.config
  (:require [mount.core :refer [defstate]]))

(defstate config
  :start (-> "env/dev/resources/config.edn" slurp read-string))
