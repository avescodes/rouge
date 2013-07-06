(ns io.rkn.rouge.game.board-test
  (:require [clojure.test :refer :all]
            [io.rkn.rouge.game.board :refer :all]))

(deftest sizeb-test
  (is (= [1 2]
         (sizeb {:grid [[0 0]]}))))

(deftest graft-piece-to-grid-test
  (let [board {:grid [[0 0]
                      [1 1]]
               :piece {:shape [[1 1]]
                       :color 2
                       :position {:row 0 :col 0}}}]
    (is (= [[2 2]
            [1 1]]
           (graft-piece-to-grid {} board)))
    (is (= [[0 0]
            [1 1]]
           (graft-piece-to-grid {} (dissoc board :piece))))
    (is (not (graft-piece-to-grid {} {})))))

(defn with-posn [posn board]
    (assoc-in board [:piece :position] posn))

(deftest collided?-test
  (let [board {:grid [[0 0 0]
                      [0 0 0]
                      [1 1 0]]
              :piece {:shape [[1]]}}
        top {:row 0 :col 0}
        hit-landed {:row 2 :col 0}
        missed-landed {:row 2 :col 2}
        hit-ground {:row 3 :col 2}]
    (is (collided? (with-posn hit-landed board)))
    (is (collided? (with-posn hit-ground board)))
    (is (not (collided? (with-posn top board))))
    (is (not (collided? (with-posn missed-landed board))))
    (is (not (collided? (dissoc board :piece))))))

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

(deftest valid-posn?-test
  (let [game {:grid [[0 0 0]
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

(deftest clearable?-test
  (is (clearable? [[1 1]]))
  (is (not (clearable? [[0 1]
                        [0 0]]))))

(deftest clear-lines-test
  (is (= {:grid [[0 0]
                 [0 2]]}
         (clear-lines {:grid [[0 2]
                              [1 1]]})))
  (is (= {:grid [[0 0]
                 [0 0]
                 [0 0]
                 [2 0]
                 [0 4]]}
         (clear-lines {:grid  [[0 0]
                               [1 1]
                               [2 0]
                               [3 3]
                               [0 4]]}))))

