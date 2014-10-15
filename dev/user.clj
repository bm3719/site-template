;;;; This is a dev namespace to contain utility functions useful for
;;;; interacting with the data at the REPL, or for general development.
(ns user
  (:require [site-template.handler :as handler]
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

(defn refresh
  "Run this to refresh the project.  This will shut down the server if it's
  active first." []
  (when (and server (= (org.eclipse.jetty.server.Server/getState server) "STARTED"))
    (.stop server))
  (repl/refresh))
