;;;; This is a dev namespace to contain utility functions useful for
;;;; interacting with the data at the REPL, or for general development.
(ns user
  (:require [site-template.handler :as handler]
            [site-template.config :as config]
            [site-template.db :as db]
            [site-template.email :as email]
            [site-template.crypto :as crypto]
            [ring.adapter.jetty :as jetty]
            [clojure.pprint :refer [pp pprint]]
            [clojure.repl :refer [doc]]
            [clojure.tools.namespace.repl :as repl]))

;; Global Var that holds the server.
(def server nil)

(defn boot
  "Run this to start the server from the REPL."
  ([join?]
     (alter-var-root
      #'server
      (constantly
       (jetty/run-jetty #'handler/app
                        {:join? join? :port 3000}))))
  ([] (boot false)))

(defn restart
  "Restart Jetty server." []
  (if (nil? server) (boot)
      (do
        (when (= (org.eclipse.jetty.server.Server/getState server) "STARTED")
          (.stop server))
        (.start server))))

;; Note: You should call C-c C-l on any AOT namespaces prior to running this
;; (e.g. security.clj).
(defn refresh
  "Run this to refresh the project.  This will shut down the server if it's
  active first." []
  (when (and server (= (org.eclipse.jetty.server.Server/getState server) "STARTED"))
    (.stop server))
  (repl/refresh))

(defn db-setup
  "Activate MongoDB connection to test database.  Run this to create a var
  named db that can then be used to pass to functions like
  db/retrieve-maps." []
  (defonce db (db/mongo-setup (db/create-conn config/host config/port)
                              config/db config/user config/pwd)))

(def test-user
  [{:name "Bruce C. Miller"
    :sexuality "Lithromantic"
    :gender "Trigender"
    :therian-species "Plantkin"
    :trans-size "Rubenesque"
    :headmates ["Steelfang Ringtails"
                "Gleep"]
    :trigger-words ["Food"
                    "Spiders"
                    "Monkey necklace"]
    :trans-ethnicity "Eskimo"}
   {:name "Erik J. Seppanen"
    :sexuality "Pomosexual"
    :gender "Genderfluid"
    :therian-species "Shloof"
    :trans-size "Transthin"
    :headmates ["Indigo Child"
                "Pyrofox"]
    :trigger-words ["Uncle Gary"
                    "The"
                    "Homogenous"]
    :trans-ethnicity "Finnish"}])

;; (db/batch-insert-maps db "test-user" test-user)
