(ns user
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.pprint :refer (pprint)]
            [clojure.repl :refer :all]
            [clojure.test :as test]
            [clojure.tools.namespace.repl :refer (refresh refresh-all)]
            [lanterna.screen :as lanterna]
            [io.rkn.tetra :as t]
            [io.rkn.tetra.board :as b]
            [io.rkn.tetra.game :as g]
            [io.rkn.tetra.system :as system]))

(def game nil)

(defn init
  "Constructs the current development system."
  []
  (set! *print-length* 5)
  (alter-var-root #'game
                  (constantly (system/new-game :swing [10 5]))))

(defn start []
  (future
    (lanterna/in-screen (:screen game)
                        (system/run-game game))))

(defn stop []
  (lanterna/stop (:screen game)))

(defn reset []
  (stop)
  (refresh :after 'user/go))

(defn go []
  (init)
  (start))

