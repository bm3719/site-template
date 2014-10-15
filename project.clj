(defproject site-template "0.1.0-SNAPSHOT"
  :description "A template that can be used for a secure Ring/Compojure site."
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [cheshire "5.3.1"]
                 [compojure "1.2.0"]
                 [com.novemberain/monger "2.0.0"]
                 [de.ubercode.clostache/clostache "1.4.0"]
                 [javax.mail/mail "1.4.3"]
                 [ring/ring-jetty-adapter "1.3.1"]
                 [org.eclipse.jetty/jetty-servlet "7.6.1.v20120215"]
                 [org.mongodb/mongo-java-driver "2.12.3"]
                 [org.slf4j/slf4j-api "1.7.7"]
                 [org.slf4j/slf4j-log4j12 "1.7.7"]
                 [org.slf4j/jcl-over-slf4j "1.7.7"]
                 [org.springframework.security/spring-security-web "3.2.4.RELEASE"]
                 [org.springframework.security/spring-security-config "3.2.4.RELEASE"]]
  :plugins [[lein-ring "0.8.12"]]
  :ring {:handler site-template.handler/app}
  :jvm-opts ["-Xmx3g"]
  :main site-template.handler
  :repl-options {:init-ns user}
  :aot [site-template.security]
  :profiles {:dev {:resource-paths ["dev"]
                   :dependencies [[javax.servlet/servlet-api "2.5"]
                                  [ring-mock "0.1.5"]
                                  [org.clojure/tools.namespace "0.2.7"]]}
             :uberjar {:aot :all}})
