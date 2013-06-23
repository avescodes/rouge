(ns io.rkn.tetra.board
  (:require [io.rkn.tetra.pieces :as p]))

;; Utility Functions
(defn sizeb
  "Game's Board's size as a row-major index (i.e. [1 2] for board [[x x]]).

   Assumes uniform sized columns."
  [game]
  (let [board (:board game)]
    [(count board) (count (first board))]))

(defn mapb
  "Map f over each element in board matrix."
  [f board]
  (mapv (fn [row] (mapv f row)) board))

(defn set-in-board
  "Set value at idx in game's :board to v. idx is in row-major order."
   [game idx v]
  (let [ks (concat [:board] idx)]
    (assoc-in game ks v)))

;; Game Logic predicates
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

(defn occupied?
  "Check if row-major idx is non-empty (0) in board"
  [board idx]
  (not= 0 (get-in board idx)))

(defn hit-bottom?
  "Check if row-major idx is below bottom of board-size."
  [board-size idx]
  (let [[row _] idx
        [rows _] board-size]
    (>= row rows)))

(defn collided?
  "Check if a game-state contains any piece-to-landed or
   piece-to-ground collisions."
  [game]
  (let [piece-idxs (p/occupied-idxs (:piece game))]
    (or (some (partial occupied? (:board game)) piece-idxs)
        (some (partial hit-bottom? (sizeb game)) piece-idxs))))

(defn valid-posn?
  "Is :piece in a valid position (inside the board and in no occupied
   spaces)?"
  [game]
  (let [piece-idxs (p/occupied-idxs (:piece game))]
    ;; Every part of the piece is inside the board
    (and (every? (partial inside-board? (sizeb game)) piece-idxs)
         (not-any? (partial occupied? (:board game)) piece-idxs))))

(defn graft-piece-to-board
  "Return the game where :piece has been shaded-in in :board."
  [game]
  (let [piece (:piece game)]
    (loop [new-game game
           [idx & remaining-idxs] (p/occupied-idxs piece)]
      (if (nil? idx)
        new-game
        (recur (set-in-board new-game idx (:color piece))
               remaining-idxs)))))

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
