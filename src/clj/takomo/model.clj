(ns takomo.model
  (:require [clojure.spec.alpha :as s])
  (:import [java.lang Long]))

(defn long? [x]
  (instance? Long x))

(s/def :db/id int?)

(s/def ::id int?)

(s/def :member/firstname string?)
(s/def :member/lastname string?)
(s/def :member/email string?)
(s/def :member/salt string?)
(s/def :member/passhash string?)
(s/def :member/password string?)
(s/def :member/role #{:admin :manager :accountant :employee :external})
(s/def :new-member/role #{"admin" "manager" "accountant" "employee" "external"})
(s/def ::member (s/keys :opt-un [:db/id :member/firstname :member/lastname :member/email :member/password :member/role]))
(s/def ::new-member (s/keys :opt-un [:member/firstname :member/lastname :member/email :member/password :new-member/role]))
(s/def ::members (s/coll-of ::member))

(s/def :company/name string?)
(s/def :company/contact string?)
(s/def :company/department string?)
(s/def :company/city string?)
(s/def :company/street string?)
(s/def :company/postal string?)
(s/def :company/country string?)
(s/def ::new-company (s/keys :opt-un [:company/name
                                      :company/contact
                                      :company/department
                                      :company/city
                                      :company/street
                                      :company/postal
                                      :company/country]))
(s/def ::company (s/keys :opt-un [:db/id
                                  :company/name
                                  :company/contact
                                  :company/department
                                  :company/city
                                  :company/street
                                  :company/postal
                                  :company/country]))
(s/def ::companies (s/coll-of ::company))

(s/def :document/reference string?)
(s/def :document/fileName string?)
(s/def ::document (s/keys :opt-un [:db/id :document/fileName :document/reference]))
(s/def ::new-document (s/keys :req-un [:document/fileName :document/reference]))
(s/def ::documents (s/coll-of ::document))

(s/def :task/title string?)
(s/def :task/description string?)
(s/def :task/assignee #(or (nil? %) (int? %)))
(s/def :task/estimation double?)
(s/def :task.estimation/unit keyword?)
(s/def :task/reference string?)
(s/def :task/project ::id)

(s/def ::new-task (s/keys :opt-un [:task/title :task/description :task/assignee :task/estimation :task.estimation/unit :task/project :task/reference]))
(s/def ::task (s/keys :opt-un [:db/id :task/title :task/description :task/assignee :task/estimation :task.estimation/unit :task/project :task/reference]))
(s/def ::tasks (s/coll-of ::task))

(s/def :effort/startDate #(or (string? %) (nil? %)))
(s/def :effort/endDate #(or (string? %) (nil? %)))
(s/def :effort/task ::id)
(s/def :effort/assignee ::id)
(s/def ::new-effort (s/keys :opt-un [:effort/startDate :effort/endDate :effort/task :effort/assignee]))
(s/def ::effort (s/keys :opt-un [:db/id :effort/startDate :effort/endDate :effort/task :effort/assignee]))
(s/def ::efforts (s/coll-of ::effort))

(s/def :project/title string?)
(s/def :project/description string?)
(s/def :project/startDate string?)
(s/def :project/endDate string?)
(s/def :project/acceptedAt string?)
(s/def :project/paidAt string?)
(s/def :project/company ::id)
(s/def :project/responsible int?)
(s/def :project/rate int?)
(s/def :project.rate/unit keyword?)
(s/def :project/id string?)
(s/def :project/members (s/coll-of ::id))
(s/def :project/budget int?)
(s/def ::new-project (s/keys :opt-un [:project/title
                                      :project/description
                                      :project/responsible
                                      :project/startDate
                                      :project/endDate
                                      :project/acceptedAt
                                      :project/paidAt
                                      :project/company
                                      :project/members
                                      :project/budget
                                      :project/rate
                                      :project.rate/unit]))
(s/def ::project (s/keys :opt-un [:db/id
                                  :project/id
                                  :project/title
                                  :project/description
                                  :project/responsible
                                  :project/startDate
                                  :project/endDate
                                  :project/acceptedAt
                                  :project/paidAt
                                  :project/company
                                  :project/members
                                  :project/budget
                                  :project/rate
                                  :project.rate/unit]))

(s/def ::projects (s/coll-of ::project))

(s/def :jws/role string?)
(s/def :jws/token string?)
(s/def :jws/expired string?)
(s/def ::jws (s/keys :opt-un [:jws/token :jws/role :jws/expired]))

(s/def :credentials/username string?)
(s/def :credentials/password string?)
(s/def ::credentials (s/keys :opt-un [:credentials/username :credentials/password]))

(s/def :turnover/salesData string?)
(s/def :turnover/bookingDay #(or (string? %) (nil? %)))
(s/def :turnover/amount int?)
(s/def :turnover/unit keyword?)
(s/def :turnover/remark string?)
(s/def :turnover/invoice int?)
(s/def :turnover/filename string?)
(s/def ::new-turnover (s/keys :opt-un [:turnover/salesData
                                       :turnover/bookingDay
                                       :turnover/amount
                                       :turnover/unit
                                       :turnover/remark
                                       :turnover/invoice
                                       :turnover/filename]))

(s/def ::turnover (s/keys :opt-un [:db/id
                                   :turnover/salesData
                                   :turnover/bookingDay
                                   :turnover/amount
                                   :turnover/unit
                                   :turnover/remark
                                   :turnover/invoice
                                   :turnover/filename]))
(s/def ::turnovers (s/coll-of ::turnover))
