(ns site-template.db
  (:require [monger.core :as m]
            [monger.command :as cmd]
            [monger.collection :as col]
            [monger.conversion :as conv :refer [from-db-object]]
            [monger.result :as res]
            [monger.query :as q])
  (:import [org.bson.types ObjectId]
           [com.mongodb MongoClient DB WriteConcern MongoClientOptions])
  (:gen-class))

(defn create-conn
  "Creates the connection to MongoDB." [host port]
  (m/connect (m/server-address host port) (.. MongoClientOptions builder build)))

(defn mongo-setup
  "Sets connection parameters and returns the DB object, under the assumption
  that a single DB will be used." [conn db db-user db-pwd]
  (m/authenticate (m/get-db conn db) db-user (.toCharArray db-pwd))
  (m/set-default-write-concern! WriteConcern/JOURNAL_SAFE)
  (m/get-db conn db))

;;; Data insertion.
(defn insert-map
  "Given a collection, insert a map as a document."
  [db col m]
  (res/ok? (col/insert db col map)))
(defn batch-insert-maps
  "Given a collection, insert a vector of maps."
  [db col v]
  (res/ok? (col/insert-batch db col v)))

;;; Data retrieval.
(defn retrieve-maps
  "Get all the documents from a collection."
  [db col]
  (col/find-maps db col))
(defn retrieve-map
  "Get a document from a collection, given some criteria.
  e.g. (retrieve-map \"person\" {:nameFirst \"JOHN\"})"
  [db col m]
  (col/find-one-as-map db col m))

(defn mongo-shutdown
  "Disconnect from MongoDB." [conn]
  (m/disconnect conn))
