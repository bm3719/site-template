(ns site-template.handler
  (:require [site-template.config :as config]
            [site-template.db :as db]
            [site-template.security :as security]
            [compojure.core :refer :all]
            [compojure.handler :as handler]
            [compojure.route :as route])
  (:import [org.springframework.security.core.context SecurityContextHolder]
           [org.eclipse.jetty.servlet ServletHolder ServletContextHandler FilterHolder]
           [org.springframework.web.context ContextLoaderListener]
           [org.springframework.web.filter DelegatingFilterProxy]
           [java.security Security]
           [java.security.cert PKIXParameters]
           [java.security KeyStore]))

(defn wrap-reload-spring [app]
  "Reload the Spring application context on every HTTP request."
  (fn [req]
    (let [application-context (security/application-context req)]
      (app req))))

(defroutes app-routes
  (GET "/" [] "Hello World")
  (route/resources "/")
  (route/not-found "Not Found"))

(def app (handler/site app-routes))

