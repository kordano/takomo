(ns takomo.store.turnover
  (:require [datahike.api :as d]
            [takomo.store :refer [conn]]
            [takomo.utils :refer [add-namespace remove-namespace str->Date]]))

(def turnover-keys
  [:turnover/salesData
   :turnover/bookingDay
   :turnover/amount
   :turnover/unit
   :turnover/remark
   :turnover/invoice
   :turnover/filename])


(defn pre-process [{:keys [bookingDay amount] :as turnover}]
  (letfn [(update-date [t]
            (if bookingDay
              (update t :turnover/bookingDay str->Date)
              t))
          (int->long [t k] (if (get t k) (update t k long) t))]
    (-> turnover
        (add-namespace :turnover)
        update-date
        (int->long :turnover/amount)
        (int->long :turnover/invoice)
        (select-keys turnover-keys))))

(defn post-process [turnover]
  (-> turnover
      remove-namespace))

(defn create-turnover [new-turnover]
  (d/transact conn [(pre-process new-turnover)]))
