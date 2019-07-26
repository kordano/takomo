(ns takomo.core
  (:require [takomo.store :as store]
            [takomo.server :as server]))

(defn -main [& args]
  (store/init)
  (server/start-server))

(comment

  (-main)

  )
