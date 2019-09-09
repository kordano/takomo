(ns takomo.core
  (:require [mount.core :as mount]))

(defn -main [& args]
  (mount/start))

(comment

  (mount/start)

  (mount/stop)

  (-main))
