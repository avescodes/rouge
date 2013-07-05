(ns io.rkn.rouge.game.grid-test
  (:require [clojure.test :refer :all]
            [io.rkn.rouge.game.grid :refer :all]))

(deftest empty-grid-test
  (is (= [[:foo :foo]]
         (empty-grid 1 2 :foo))))

(deftest size-test
  (is (= [1 2]
         (size [[0 0]]))))

(deftest set-in-grid-test
  (is (= [[0 :foo]]
         (set-in-grid [[0 0]]
                       [0 1]
                       :foo))))
