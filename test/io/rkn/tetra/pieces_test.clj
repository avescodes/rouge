(ns io.rkn.tetra.pieces-test
  (:require [clojure.test :refer :all]
            [io.rkn.tetra.pieces :refer :all]))

(deftest next-rotation-shape-test
  (let [one-rotation-piece {:shape :a
                            :rotations []}]
    (is (= one-rotation-piece (next-rotation-shape one-rotation-piece))))
  (let [two-rotation-piece {:shape :a
                            :rotations [:b]}]
    (is (= {:shape :b
            :rotations [:a]}
           (next-rotation-shape two-rotation-piece)))
    (is (= two-rotation-piece
           (-> two-rotation-piece
               next-rotation-shape
               next-rotation-shape))))
  (let [four-rotation-piece {:shape :a
                             :rotations [:b :c :d]}]
    (is (= {:shape :b
            :rotations [:c :d :a]}
           (next-rotation-shape four-rotation-piece)))
    (is (= {:shape :c
            :rotations [:d :a :b]}
           (-> four-rotation-piece next-rotation-shape next-rotation-shape)))
    (is (= {:shape :d
            :rotations [:a :b :c]}
           (-> four-rotation-piece next-rotation-shape next-rotation-shape next-rotation-shape)))
    (is (= {:shape :a
            :rotations [:b :c :d]}
           (-> four-rotation-piece next-rotation-shape next-rotation-shape next-rotation-shape next-rotation-shape)))))
