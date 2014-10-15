(ns site-template.email
  (:require [site-template.config :as config]
            [clostache.parser :as clostache])
  (:import [javax.mail Authenticator]
           [java.util Properties]))

(def pending-s "IRCOP DoNotReply Email Service - User Account Pending")
(def pending-b (str "Your IRCOP user account is pending approval. "
                    "Please contact the system administration for further information. "
                    "Please do not reply to this email. Thank you."))
(def approved-s "IRCOP DoNotReply Email Service - User Account Approved")
(def approved-b (str "Your IRCOP user account has been approved. "
                     "Please contact the system administration for further information. "
                     "Please do not reply to this email. Thank you."))
(def deactivated-s "IRCOP DoNotReply Email Service - User Account Deactivated")
(def deactivated-b (str "Your IRCOP user account has been deactivated. "
                        "Please contact the system administration for further information. "
                        "Please do not reply to this email. Thank you."))

;; TODO: Test this.
(defn- mail [& m]
  (let [mail (apply hash-map m)
        props (java.util.Properties.)]
    (doto props
      (.put "mail.smtp.host" (:host mail))
      (.put "mail.smtp.port" (:port mail))
      (.put "mail.smtp.user" (:user mail))
      (.put "mail.smtp.socketFactory.port" (:port mail))
      (.put "mail.smtp.auth" "true"))
    (if (= (:ssl mail) true)
      (doto props
        (.put "mail.smtp.starttls.enable" "true")
        (.put "mail.smtp.socketFactory.class"
              "javax.net.ssl.SSLSocketFactory")
        (.put "mail.smtp.socketFactory.fallback" "false")))
    (let [authenticator (proxy [javax.mail.Authenticator] []
                          (getPasswordAuthentication
                            []
                            (javax.mail.PasswordAuthentication.
                             (:user mail) (:password mail))))
          recipients (reduce #(str %1 "," %2) (:to mail))
          session (javax.mail.Session/getInstance props authenticator)
          msg     (javax.mail.internet.MimeMessage. session)]
      (.setFrom msg (javax.mail.internet.InternetAddress. (:user mail)))
      (.setRecipients msg
                      (javax.mail.Message$RecipientType/TO)
                      (javax.mail.internet.InternetAddress/parse recipients))
      (.setSubject msg (:subject mail))
      (.setText msg (:text mail))
      (javax.mail.Transport/send msg))))

(defn- send-email
  "Sends an email, using settings from config."
  [to-email-col subject body]
  (if (and config/production-mode (not (nil? (not-empty to-email-col ))))
    (mail :user config/master-email-address
          :password ""
          :host config/smtp-ip
          :port config/smtp-port
          :ssl false
          :to to-email-col
          :subject subject
          :text body)
    (println (str "Sending email to: " to-email-col "\n Subject: " subject " Body:" body))))

(defn send-mail-by-user-status
  "Checks the user object and sends the appropriate status change email.  Call
  prior to saving the user to the database." [user db-user]
  (let [db-has-requested? (every? :requested (:role db-user))
        new-has-requested? (every? :requested (:role user))
        is-active? (:is_active user)
        db-is-active? (:is_active db-user)
        to-email (:email user)]
    (cond (and is-active? new-has-requested? (not db-has-requested?)) (send-email to-email pending-s pending-b)
          (and is-active? db-has-requested? (not new-has-requested?)) (send-email to-email approved-s approved-b)
          (and (not is-active?) db-is-active?) (send-email to-email deactivated-s deactivated-b))))

(defn send-mail-by-transfer-request
  "Checks the transfer_request object being saved and sends the appropriate email to
  the users involved." [request status clpso-emails]
  (let [temp-request {:request request :status status :archived (if (nil? status) false true)}
        subject (clostache/render-resource "email-templates/transfer-request-subject.mustache" temp-request)
        body (clostache/render-resource "email-templates/transfer-request-body.mustache" temp-request)
        to (if (nil? status) clpso-emails [(:requester_email request)])]
    (send-email to subject body)))

        

