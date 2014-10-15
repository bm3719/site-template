(ns site-template.config
  (:gen-class))

;; A flag specifying whether the application is in development or production
;; mode.  Turn this on to enable sending of email.
(def production-mode false)

;; Datastore settings.
(def host "localhost")
(def port 27017)
(def db "fake-data")
(def user "ircop")
(def pwd "ircop")

(def encrypt-pwd "k94ja7d1jv02lauv")

;; Email settings.
(def smtp-ip "143.69.251.35")
(def smtp-host "bulksmtp.us.army.mil")
(def smtp-port "25")
;; TODO: Change this to the real email address later.
(def master-email-address "josh.lents@us.army.mil")
