(ns io.rkn.tetra.board-test
  (:require [clojure.test :refer :all]
            [io.rkn.tetra.system :refer [new-game]]
            [io.rkn.tetra.board :refer :all]))

(deftest sizeb-test
  (is (= [1 2]
         (sizeb {:board [[0 0]]}))))

(deftest empty-board-test
  (is (= [[:foo :foo]]
         (empty-board 1 2 :foo))))

(deftest mapb-test
  (is (= [[1 1]]
         (mapb inc [[0 0]]))))

(deftest set-in-board-test
  (is (= {:board [[0 :foo]]}
         (set-in-board {:board [[0 0]]}
                       [0 1]
                       :foo))))

(deftest inside-board?-test
  (let [size [2 3]]
    (is (inside-board? size [0 0]))
    (is (inside-board? size [1 1]))
    (is (not (inside-board? size [-1 0])))
    (is (not (inside-board? size [0 -1])))
    (is (not (inside-board? size [2 0])))
    (is (not (inside-board? size [0 3])))))

(deftest occupied?-test
  (let [board [[0 1]]]
    (is (occupied? board [0 1]))
    (is (not (occupied? board [0 0])))))

(deftest hit-bottom?-test
  (let [size [1 1]]
    (is (hit-bottom? size [1 0]))
    (is (not (hit-bottom? size [0 0])))))

(defn with-posn [posn game]
  (assoc-in game [:piece :position] posn))

(deftest collided?-test
  (let [game {:board [[0 0 0]
                      [0 0 0]
                      [1 1 0]]
              :piece {:shape [[1]]}}
        top {:row 0 :col 0}
        hit-landed {:row 2 :col 0}
        missed-landed {:row 2 :col 2}
        hit-ground {:row 3 :col 2}]
    (is (collided? (with-posn hit-landed game)))
    (is (collided? (with-posn hit-ground game)))
    (is (not (collided? (with-posn top game))))
    (is (not (collided? (with-posn missed-landed game))))))

(deftest valid-posn?-test
  (let [game {:board [[0 0 0]
                      [0 0 0]
                      [1 1 0]]
              :piece {:shape [[1]
                              [1]]}}
        top-left {:row 0 :col 0}
        almost-ground {:row 1 :col 2}
        hit-landed {:row 1 :col 0}
        hit-ground {:row 2 :col 2}
        out-top {:row -1 :col 0}
        out-left {:row 0 :col -1}
        out-right {:row 0 :col 3}]
    (is (valid-posn? (with-posn top-left game)))
    (is (valid-posn? (with-posn almost-ground game)))
    (is (not (valid-posn? (with-posn hit-landed game))))
    (is (not (valid-posn? (with-posn hit-ground game))))
    (is (not (valid-posn? (with-posn out-top game))))
    (is (not (valid-posn? (with-posn out-left game))))
    (is (not (valid-posn? (with-posn out-right game))))))

(deftest graft-piece-to-board-test
  (let [game {:board [[0 0]
                      [1 1]]
              :piece {:shape [[1 1]]
                      :color 2
                      :position {:row 0 :col 0}}}]
    (is (= (assoc game :board [[2 2]
                               [1 1]])
           (graft-piece-to-board game)))))
