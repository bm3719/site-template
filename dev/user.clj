;;;; This is a dev namespace to contain utility functions useful for
;;;; interacting with the data at the REPL, or for general development.
(ns user
  (:require [site-template.handler :as handler]
            [site-template.config :as config]
            [site-template.db :as db]
            [site-template.email :as email]
            [site-template.crypto :as crypto]
;            [site-template.security :as security]
            [ring.adapter.jetty :as jetty]
            [clojure.pprint :refer [pp pprint]]
            [clojure.repl :refer [doc]]
            [clojure.tools.namespace.repl :as repl]
            [cheshire.generate :as generate]
            [cheshire.core :as cheshire]))

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
        (when (nil? db/db)
          (db/db-setup))
        (.start server))))

;; Note: You should manually load (in Emacs: C-c C-l) the
;; site_template/security.clj file prior to running this, if it's a dependency
;; anywhere.  You only have to do this once.
(defn refresh
  "Run this to refresh the project.  This will shut down the server if it's
  active first.  If you get a compilation error, you may have to manually run:

  (clojure.tools.namespace.repl/refresh)" []
;  (load-file "src/site_template/security.clj")
  (when (and server (= (org.eclipse.jetty.server.Server/getState server) "STARTED"))
    (.stop server))
  (when (not= db/conn nil)
    (db/mongo-shutdown db/conn))
  (repl/refresh))

(def test-user
  [{:name "Bruce C. Miller"
    :sexuality "Lithromantic"
    :gender "Trigender"
    :therian-species "Plantkin"
    :trans-size "Rubenesque"
    :headmates ["Steelfang Ringtails"
                "Gleep"]
    :trigger-words ["Food"
                    "Homogenous"
                    "Monkey necklace"]
    :trans-ethnicity "German"}
   {:name "Erik J. Seppanen"
    :sexuality "Pomosexual"
    :gender "Genderfluid"
    :therian-species "Shloof"
    :trans-size "Transthin"
    :headmates ["Indigo Child" 
                "Pyrofox"]
    :trigger-words ["Uncle Gary"
                    "The"
                    "Fishmonger"]
    :trans-ethnicity "Finnish"}
   {:name "Josh Q. Lents"
    :sexuality "Autosexual"
    :gender "Bi-androgynous"
    :therian-species "Pandakin"
    :trans-size "Rubenesque"
    :headmates ["Wayne Border"
                "President Obama"]
    :trigger-words ["Doctor"]
    :trans-ethnicity "German"}])

(defn create-test-data
  "Inserts the test map into MongoDB.  Run db-setup first.  Only needs to be
  run once." []
  (db/put-maps "test-user" test-user))
