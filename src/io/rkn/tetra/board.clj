(ns io.rkn.tetra.board
  (:require [io.rkn.tetra.pieces :as p]))

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
