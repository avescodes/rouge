(ns io.rkn.tetra.pieces)

(defn transpose [m]
  (apply mapv vector m))

(def tetras
  {:square {:shape [[1 1]
                    [1 1]]
            :position {:row 0, :col 0}
            :color 1
            :rotations []}
   :line {:shape [[1]
                  [1]
                  [1]
                  [1]]
          :position {:row 0 :col 0}
          :color 2
          :rotations [[[1 1 1 1]]]}
   :t {:shape [[1 1 1]
               [0 1 0]]
       :position {:row 0 :col 0}
       :color 3
       :rotations [[[0 1]
                    [1 1]
                    [0 1]]
                   [[0 1 0]
                    [1 1 1]]
                   [[1 0]
                    [1 1]
                    [1 0]]]}
   :j {:shape [[1 1]
               [1 0]
               [1 0]]
       :position {:row 0 :col 0}
       :color 4
       :rotations [[[1 1 1]
                    [0 0 1]]
                   [[0 1]
                    [0 1]
                    [1 1]]
                   [[1 0 0]
                    [1 1 1]]]}
   :l {:shape [[1 1]
               [0 1]
               [0 1]]
       :position {:row 0 :col 0}
       :color 5
       :rotations [[[0 0 1]
                    [1 1 1]]
                   [[1 0]
                    [1 0]
                    [1 1]]
                   [[1 1 1]
                    [1 0 0]]]}
   :z {:shape [[0 1]
               [1 1]
               [1 0]]
       :position {:row 0 :col 0}
       :color 6
       :rotations [[[1 1 0]
                    [0 1 1]]]}
   :s {:shape [[1 0]
               [1 1]
               [0 1]]
       :position {:row 0 :col 0}
       :color 7
       :rotations [[[0 1 1]
                    [1 1 0]]]}})

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

;; Rotation
(defn next-rotation-shape [piece]
  (let [{:keys [shape rotations]} piece
        [new-shape & new-rotations] (conj rotations shape)]
    (-> piece
        (assoc :shape new-shape)
        (assoc :rotations (vec new-rotations)))))
