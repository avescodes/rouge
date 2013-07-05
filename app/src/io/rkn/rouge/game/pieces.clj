(ns ^:shared io.rkn.rouge.game.pieces
  (:require [io.rkn.rouge.game.grid :as g]))

(def tetras
  {:square {:shape [[1 1]
                    [1 1]]
            :position {:row 0, :col 4}
            :color 1}
   :line {:shape [[0 0 0 0]
                  [1 1 1 1]
                  [0 0 0 0]
                  [0 0 0 0]]
          :position {:row -1 :col 3}
          :color 2}
   :t {:shape [[0 1 0]
               [1 1 1]
               [0 0 0]]
       :position {:row 0 :col 3}
       :color 3}
   :j {:shape [[1 0 0]
               [1 1 1]
               [0 0 0]]
       :position {:row 0 :col 3}
       :color 4}
   :l {:shape [[0 0 1]
               [1 1 1]
               [0 0 0]]
       :position {:row 0 :col 3}
       :color 5}
   :z {:shape [[1 1 0]
               [0 1 1]
               [0 0 0]]
       :position {:row 0 :col 3}
       :color 6}
   :s {:shape [[0 1 1]
               [1 1 0]
               [0 0 0]]
       :position {:row 0 :col 3}
       :color 7}})

(defn random-tetra []
  (rand-nth (vals tetras)))

(defn occupied-idxs
  "Produce a sequence of all the indices a given piece occupies.

  Example:
  - Square at row: 1 would be [[1 0] [1 1] [2 0] [2 1]]"
  [piece]
  (let [shape (:shape piece)
        rows (count shape)
        cols (count (first shape))
        {offset-row :row offset-col :col} (:position piece)]
    (for [row (vec (range rows))
          col (vec (range cols))
          :when (not= 0 (get-in shape [row col]))]
      [(+ row offset-row)
       (+ col offset-col)])))

(defn rotate-idx [size idx]
  (when (not (apply = size))
    (throw (ex-info "rotate-size only handles rotation of square matrices." {:size size})))
  (let [s (- (first size) 1)
        [y x] idx]
    [x (- s y)]))

(defn rotate-shape [shape]
  (let [size (g/size shape)
        new-shape (apply g/empty-grid size)]
    (loop [grid new-shape
          [idx & remaining] (occupied-idxs {:shape shape
                                            :position {:row 0 :col 0}})]
      (if idx
        (let [rotated-idx (rotate-idx size idx)]
          (recur (assoc-in grid rotated-idx (get-in shape idx))
                 remaining))
        grid))))
