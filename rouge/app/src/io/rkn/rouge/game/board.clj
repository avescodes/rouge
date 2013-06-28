(ns ^:shared io.rkn.rouge.game.board
  (:require [io.rkn.rouge.game.pieces :as p]))

;; Board creation
(def create-vector (comp vec repeat))

(defn empty-board
  ([rows cols fill] (create-vector rows (create-vector cols fill)))
  ([rows cols] (empty-board rows cols 0)))
;;
;; Utility Functions
(defn sizeg
  "Game grid's size as a row-major index (i.e. [1 2] for grid [[x x]]).

   Assumes uniform sized columns."
  [grid]
  [(count grid) (count (first grid))])
(defn sizeb [board] (sizeg (:grid board)))

(defn mapg
  "Map f over each element in grid."
  [f grid]
  (mapv (fn [row] (mapv f row)) grid))

(defn set-in-grid
      "Set value at idx in board to v. idx is in row-major order."
      [grid idx v]
      (assoc-in grid idx v))

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

(defn graft-piece-to-grid
    "Return a board where piece has been grafted to grid."
  ([_ board] (graft-piece-to-grid board))
  ([{:keys [grid piece]}]
   (if (and grid piece)
     (loop [grafted-grid grid
            [idx & remaining-idxs] (p/occupied-idxs piece)]
       (if (nil? idx)
         grafted-grid
         (recur (set-in-grid grafted-grid idx (:color piece))
                remaining-idxs)))
     grid)))
