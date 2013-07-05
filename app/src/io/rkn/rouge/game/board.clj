(ns ^:shared io.rkn.rouge.game.board
  (:require [io.rkn.rouge.game.grid :as g]
            [io.rkn.rouge.game.pieces :as p]))

(defn sizeb [board] (g/size (:grid board)))

;; Game Logic predicates
(defn occupied?
  "Check if row-major idx is non-empty (0) in grid"
  [grid idx]
  (not= 0 (get-in grid idx)))

(defn hit-bottom?
  "Check if row-major idx is below bottom of board-size."
  [board-size idx]
  (let [[row _] idx
        [rows _] board-size]
    (>= row rows)))

(defn collided?
  "Check if a game-state contains any piece-to-landed or
   piece-to-ground collisions."
  [{:keys [grid piece] :as board}]
  (let [piece-idxs (p/occupied-idxs piece)]
    (or (some (partial occupied? grid) piece-idxs)
        (some (partial hit-bottom? (sizeb board)) piece-idxs))))

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
  [{:keys [grid piece] :as board}]
  (let [piece-idxs (p/occupied-idxs piece)]
    ;; Every part of the piece is inside the board
    (and (every? (partial inside-board? (g/size grid)) piece-idxs)
         (not-any? (partial occupied? grid) piece-idxs))))

(defn graft-piece-to-grid
    "Return a board where piece has been grafted to grid."
  ([_ board] (graft-piece-to-grid board))
  ([{:keys [grid piece]}]
   (if (and grid piece)
     (loop [grafted-grid grid
            [idx & remaining-idxs] (p/occupied-idxs piece)]
       (if (nil? idx)
         grafted-grid
         (recur (g/set-in-grid grafted-grid idx (:color piece))
                remaining-idxs)))
     grid)))

(defn full-row?
  "Return true if a vector row contains no zero elements."
  [row] (not-any? zero? row))

(defn clearable?
  "Return truthy if grid has any full rows"
  [grid]
  (some full-row? grid))

(defn clear-lines
  "Remove all full-rows from the grid, replacing them with new empty rows at
  the top of the grid."
  [{:keys [grid] :as board}]
  (let [[rows cols] (g/size grid)
        cleared-grid (remove full-row? grid)
        lines-needed (- rows (count cleared-grid))
        new-grid (into [] (concat (g/empty-grid lines-needed cols)
                                   cleared-grid))]
    (assoc board :grid new-grid)))
