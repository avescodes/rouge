(ns io.rkn.tetra.board
  (:require [io.rkn.tetra.pieces :as p]))

;; Board creation
(def create-vector (comp vec repeat))

(defn empty-board
  ([rows cols fill] (create-vector rows (create-vector cols 0)))
  ([rows cols] (empty-board rows cols 0)))

;; Utility Functions
(defn mapb [f board]
  (mapv (fn [row] (mapv f row)) board))

(defn set-in-board
  "Set value at coords in game's :board to v"
  [game coords v]
  (let [ks (concat [:board] coords)]
    (assoc-in game ks v)))

;; Game Logic predicates
(defn inside-board? [board-size coords]
  (let [[row col] coords
        [rows cols] board-size]
    (and (>= row 0)
         (< row rows)
         (>= col 0)
         (< col cols))))

(defn occupied? [board coords]
  (not= 0 (get-in board coords)))

(defn hit-bottom? [board-size coords]
  (let [[row _] coords
        [rows _] board-size]
    (>= row rows)))

(defn collided? [game]
  (let [piece-idxs (p/occupied-idxs (:piece game))]
    (or (some (partial occupied? (:board game)) piece-idxs)
        (some (partial hit-bottom? (:size game)) piece-idxs))))

(defn valid-posn?
  "Is :piece in a valid position?"
  [game]
  (let [piece-idxs (p/occupied-idxs (:piece game))]
    ;; Every part of the piece is inside the board
    (and (every? (partial inside-board? (:size game)) piece-idxs)
         (not-any? (partial occupied? (:board game)) piece-idxs))))

(defn graft-piece-to-board [game]
  (let [piece (:piece game)]
    (loop [new-game game
           [idx & remaining-idxs] (p/occupied-idxs piece)]
      (if (nil? idx)
        new-game
        (recur (set-in-board new-game idx (:color piece))
               remaining-idxs)))))

