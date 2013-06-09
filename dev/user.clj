(ns user
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.pprint :refer (pprint)]
            [clojure.repl :refer :all]
            [clojure.test :as test]
            [clojure.tools.namespace.repl :refer (refresh refresh-all)]
            [clojure.core.matrix :as m]
            [io.rkn.tetra :as t]
            [io.rkn.tetra :as b]
            [io.rkn.tetra.system :as system]))

(def game nil)

(defn init
  "Constructs the current development system."
  []
  (alter-var-root #'game
                  (constantly (system/new-game))))

(set! *print-length* 5)
