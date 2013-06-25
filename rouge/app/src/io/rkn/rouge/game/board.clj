(ns ^:shared io.rkn.rouge.game.board
  (:require [io.rkn.rouge.game.pieces :as p]))

;; Board creation
(def create-vector (comp vec repeat))

(defn empty-board
  ([rows cols fill] (create-vector rows (create-vector cols fill)))
  ([rows cols] (empty-board rows cols 0)))

(defn set-in-grid
      "Set value at idx in board to v. idx is in row-major order."
      [grid idx v]
      (assoc-in grid idx v))

(defn graft-piece-to-grid
    "Return a board where piece has been grafted to grid."
    [_  {:keys [grid piece]}]
    (loop [grafted-grid grid
            [idx & remaining-idxs] (p/occupied-idxs piece)]
      (if (nil? idx)
        grafted-grid
        (recur (set-in-grid grafted-grid idx (:color piece))
               remaining-idxs))))
