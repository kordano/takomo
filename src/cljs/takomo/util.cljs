(ns takomo.util
  (:require [accountant.core :as acc]))

(defn logout [state]
  (reset! state {})
  (.removeItem (.-localStorage js/window) "credentials")
  (acc/navigate! "/"))
