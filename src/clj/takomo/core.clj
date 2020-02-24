(ns takomo.core
  (:gen-class)
  (:require [mount.core :as mount]
            [takomo
             store
             server]))

(defn -main [& args]
(println (mount/start)))

(comment

(mount/start)

(mount/stop)

(-main))
