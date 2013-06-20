(ns io.rkn.tetra.game-test
  (:require [clojure.test :refer :all]
            [io.rkn.tetra.system :refer [new-game]]
            [io.rkn.tetra.game :refer :all]))

(deftest level-test
  (are [expected game]
       (is (= expected (level game)))
       1 {:lines-cleared 0}
       1 {:lines-cleared 9}
       2 {:lines-cleared 10}))

(deftest bump-score-test
  (are [original-game cleared expected]
       (is (= expected (bump-score original-game cleared)))
       {:score 0 :lines-cleared 0} 4 {:score 800 :lines-cleared 4}
       {:score 1000 :lines-cleared 10} 2 {:score 1600 :lines-cleared 12}))
