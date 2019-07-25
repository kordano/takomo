(ns takomo.utils
  (:require [clj-time.core :as t]
            [clj-time.coerce :as tc]
            [clj-time.format :as tf]))

(defmulti convert-to-joda-time class)

(defmethod convert-to-joda-time java.util.Date [d]
  (tc/from-date d))

(defmethod convert-to-joda-time java.lang.Long [d]
  (tc/from-long d))

(defmethod convert-to-joda-time String [d]
  (tc/from-string d))

(defn get-current-iso-8601-date
  "Returns current ISO 8601 compliant date."
  []
  (let [current-date-time (t/to-time-zone (t/now) (t/default-time-zone))]
    (tf/unparse
     (tf/with-zone (tf/formatters :date-time-no-ms)
       (.getZone current-date-time))
     current-date-time)))

(defn format-to-iso-8601-date
  "Formats given date to ISO 8601 compliant date."
  [date]
  (let [date-time (t/to-time-zone (convert-to-joda-time date) (t/default-time-zone))]
    (tf/unparse
     (tf/with-zone (tf/formatters :date-time-no-ms)
       (.getZone date-time))
     date-time)))
