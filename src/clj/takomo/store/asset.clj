(ns takomo.store.asset
  (:require [datahike.api :as d]
            [takomo.store :refer [conn]]
            [takomo.utils :refer [add-namespace remove-namespace str->Date format-to-iso-8601-date]]))

(def asset-keys
  [:asset/description
   :asset/boughtAt
   :asset/price
   :asset/receipt
   :asset/categories])

(defn pre-process [{:keys [boughtAt] :as asset}]
  (letfn [(update-date [a]
            (if boughtAt
              (update a :asset/boughtAt str->Date)
              a))
          (int->long [t k] (if (get t k) (update t k long) t))]
    (-> asset
        (add-namespace :asset)
        update-date
        (int->long :asset/price)
        (select-keys asset-keys))))

(defn post-process [asset]
  (letfn [(update-date [{:keys [boughtAt] :as a}]
            (if boughtAt
              (update a :boughtAt format-to-iso-8601-date)
              a))]
    (-> asset
        remove-namespace
        update-date)))

(defn create-asset [new-asset]
  (d/transact conn [(pre-process new-asset)]))

(defn read-assets []
  (->> @conn
       (d/q '[:find [(pull ?e [*]) ...]
              :where [?e :asset/description _]])
       (mapv post-process)))

(defn update-asset [{:keys [db/id] :as asset}]
  (if-not id
    (throw (ex-info "id should not be nil" asset))
    (d/transact conn [(select-keys asset (conj asset-keys :db/id))])))

(defn delete-asset [id]
  (d/transact conn [[:db/retractEntity id]]))
