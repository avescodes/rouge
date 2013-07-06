(ns ^:shared io.rkn.rouge.game.grid)

;; Grid creation
(def create-vector (comp vec repeat))

(defn empty-grid
  ([rows cols fill] (create-vector rows (create-vector cols fill)))
  ([rows cols] (empty-grid rows cols 0)))

;; Utility Functions
(defn size
  "Game grid's size as a row-major index (i.e. [1 2] for grid [[x x]]).

   Assumes uniform sized columns."
  [grid]
  [(count grid) (count (first grid))])

(defn set-in-grid
  "Set value at idx in board to v. idx is in row-major order."
  [grid idx v]
  (assoc-in grid idx v))


