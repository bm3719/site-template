(ns site-template.security
  (:import [org.springframework.web.context.support WebApplicationContextUtils]
           [org.springframework.security.core.userdetails User]
           [org.springframework.security.core GrantedAuthority]
           [org.springframework.security.core.authority SimpleGrantedAuthority]
           [org.springframework.security.core.context SecurityContextHolder])
  (:gen-class
   :name site-template.security.TemplateUserDetailsService
   :implements [org.springframework.security.core.userdetails.UserDetailsService]))

(defn application-context
  ([req]
     (WebApplicationContextUtils/getWebApplicationContext
      (:servlet-context req)))
  ([req name]
     (WebApplicationContextUtils/getWebApplicationContext
      (:servlet-context req) name)))

;; TODO: Create user in Mongo.
(defn create-new-user [username]
  "")

(defn get-current-user []
  (.. (SecurityContextHolder/getContext) getAuthentication getPrincipal getUsername))  

(defn get-user [username]
  {:Name "dtargaryen"
   :roles ["ROLE_DEVELOPER"]}
  ;; (let [cled-user (data/get-user-by-akoid username)]
  ;;   (if cled-user cled-user (create-new-user username)))
  )

(defn ^:dynamic get-current-user-object []
  (get-user (get-current-user)))

(defn -loadUserByUsername
  ([username]
     (let [persisted-user (get-user username)]
       (User. username "" (map #(SimpleGrantedAuthority. (:Name %)) (:roles persisted-user)))))
  ([a b] (-loadUserByUsername b)))

