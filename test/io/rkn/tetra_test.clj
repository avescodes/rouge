(ns io.rkn.tetra-test
  (:require [clojure.test :refer :all]
            [io.rkn.tetra.system :refer [new-game]]
            [io.rkn.tetra :refer :all])
  (:refer-clojure :exclude [empty]))

(defn nosp
  "Remove all spaces in a string."
  [str]
  (clojure.string/replace str " " ""))
