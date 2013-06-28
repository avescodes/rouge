(ns io.rkn.tetra.board
  (:require [io.rkn.tetra.pieces :as p]))

(defn inside-board?
  "Check if a row-major idx is not out-of-bounds of
   row-major board-size"
  [board-size idx]
  (let [[row col] idx
        [rows cols] board-size]
    (and (>= row 0)
         (< row rows)
         (>= col 0)
         (< col cols))))

(defn valid-posn?
  "Is :piece in a valid position (inside the board and in no occupied
   spaces)?"
  [game]
  (let [piece-idxs (p/occupied-idxs (:piece game))]
    ;; Every part of the piece is inside the board
    (and (every? (partial inside-board? (sizeb game)) piece-idxs)
         (not-any? (partial occupied? (:board game)) piece-idxs))))

(defn full-row?
  "Return true if a vector row contains no zero elements."
  [row] (not-any? zero? row))

(defn clearable?
  "Return truthy if board has any full rows"
  [game]
  (let [board (:board game)]
    (some full-row? board)))

(defn clear-lines
  "Remove all full-rows from the board, replacing them with new empty rows at
  the top of the board."
  [game]
  (let [board (:board game)
        [rows cols] (sizeb game)
        cleared-board (remove full-row? board)
        lines-needed (- rows (count cleared-board))
        new-board (into [] (concat (empty-board lines-needed cols)
                                   cleared-board))]
    (assoc game :board new-board)))
