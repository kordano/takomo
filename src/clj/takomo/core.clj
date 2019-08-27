(ns takomo.core
  (:require [takomo.store :as store]
            [takomo.server :as server]))

(defn -main [& args]
  (println "Starting server!")
  (store/init)
  (server/start-server))

(comment

  (server/stop-server)

  (-main)


  )
