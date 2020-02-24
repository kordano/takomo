(ns takomo.store.turnover
  (:require [datahike.api :as d]
            [takomo.store :refer [conn]]
            [takomo.utils :refer [add-namespace remove-namespace str->Date format-to-iso-8601-date]]))

(def turnover-keys
  [:turnover/salesData
   :turnover/bookingDay
   :turnover/amount
   :turnover/unit
   :turnover/remark
   :turnover/invoice
   :turnover/filename])


(defn pre-process [{:keys [bookingDay] :as turnover}]
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
  (letfn [(update-date [{:keys [bookingDay] :as t}]
            (if bookingDay
              (update t :bookingDay format-to-iso-8601-date)
              t))]
    (-> turnover
        remove-namespace
        update-date)))

(defn create-turnover [new-turnover]
  (d/transact conn [(pre-process new-turnover)]))

(defn read-turnovers []
  (->> @conn
       (d/q '[:find [(pull ?e [*]) ...]
              :where
              [?e :turnover/salesData _]])
       (mapv post-process)))

(defn update-turnover [{:keys [db/id] :as turnover}]
  (if-not id
    (throw (ex-info "id should not be nil" turnover))
    (d/transact conn [(select-keys turnover (conj turnover-keys :db/id))])))

(defn delete-turnover [id]
  (d/transact conn [[:db/retractEntity id]]))
