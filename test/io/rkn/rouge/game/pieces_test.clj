(ns io.rkn.rouge.game.pieces-test
  (:require [clojure.test :refer :all]
            [io.rkn.rouge.game.pieces :refer :all]))

#_(deftest max-kick-test
  (is (= 0 (max-kick (tetras :square))))
  (is (= 1 (max-kick (tetras :t))))
  (is (= 3 (max-kick (tetras :line)))))

(deftest rotate-idx-test
  (is (= [1 3]
         (rotate-idx [4 4] [0 1])))
  (is (= [3 2]
         (rotate-idx [4 4] [1 3])))
  (is (= [2 0]
         (rotate-idx [4 4] [3 2])))
  (is (= [0 1]
         (rotate-idx [4 4] [2 0])))
  (is (thrown-with-msg? clojure.lang.ExceptionInfo #"only handles rotation of square matrices"
                        (rotate-idx [4 2] [0 0]))))

(deftest rotate-shape-test
  (is (= [[0 1]
          [0 1]]
         (rotate-shape [[1 1]
                        [0 0]]))))
