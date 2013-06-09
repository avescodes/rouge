(ns io.rkn.tetra.pieces)

(def tetras
  {:square {:shape [[1 1]
                    [1 1]]
            :position {:row 0, :col 0}
            :color 1}
   :line {:shape [[1]
                  [1]
                  [1]
                  [1]]
          :position {:row 0 :col 0}
          :color 2}
   :t {:shape [[1 1 1]
               [0 1 0]]
       :position {:row 0 :col 0}
       :color 3}
   :j {:shape [[1 1]
               [1 0]
               [1 0]]
       :position {:row 0 :col 0}
       :color 4}
   :l {:shape [[1 1]
               [0 1]
               [0 1]]
       :position {:row 0 :col 0}
       :color 5}
   :z {:shape [[0 1]
               [1 1]
               [1 0]]
       :position {:row 0 :col 0}
       :color 6}
   :s {:shape [[1 0]
               [1 1]
               [0 1]]
       :position {:row 0 :col 0}
       :color 7}})

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
(defn transpose [m]
  (apply mapv vector m))

(def rotate-shape-fns
  [(comp transpose reverse)
   transpose])

(defn rotate-piece [piece]
  (let [rotation-mod (mod (or (:rotation piece) 0)
                          2)
        rotation-fn (nth rotate-shape-fns rotation-mod)]
    (-> piece
        (assoc-in [:shape] (rotation-fn (:shape piece)))
        (assoc-in [:rotation] (inc rotation-mod)))))
