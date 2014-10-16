(ns site-template.handler
  (:require [site-template.config :as config]
            [site-template.db :as db]
;            [site-template.security :as security]
            [cheshire.core :refer :all :as cheshire]
            [compojure.core :refer :all]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [cheshire.generate :as generate])
  (:import [org.springframework.security.core.context SecurityContextHolder]
           [org.eclipse.jetty.servlet ServletHolder ServletContextHandler FilterHolder]
           [org.springframework.web.context ContextLoaderListener]
           [org.springframework.web.filter DelegatingFilterProxy]
           [java.security Security]
           [java.security.cert PKIXParameters]
           [java.security KeyStore]))

;; (defn wrap-reload-spring [app]
;;   "Reload the Spring application context on every HTTP request."
;;   (fn [req]
;;     (let [application-context (security/application-context req)]
;;       (app req))))

(defn json-200 [to-render]
  {:status 200
   :headers {"Content-Type" "text/json; charset=utf-8"
             "Cache-Control" "no-cache, no-store, must-revalidate"
             "Pragma" "no-cache"
             "Expires" "0"}
   :body (do (generate/add-encoder org.bson.types.ObjectId generate/encode-str)
             (cheshire/generate-string to-render))})

(defn json-404 []
  {:status 404
   :headers {"Content-Type" "text/html; charset=utf-8"}
   :body "Not found"})

(defn response
  "Create a response." [& args]
  (let [conn (db/create-conn config/host config/port)
        db (db/mongo-setup conn config/db config/user config/pwd)]
    (try (json-200 (db/retrieve-maps db "test-user"))
         (finally (db/mongo-shutdown conn)))))

(defroutes app-routes
  (GET "/test-user/" [] (response ""))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app (handler/site app-routes))
