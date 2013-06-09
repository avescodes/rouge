(ns io.rkn.tetra.system
  (:require [clojure.core.matrix :as m])
  (:require [io.rkn.tetra.pieces :as p]
            [io.rkn.tetra.board :as b]))

(defn new-game
  ([] (new-game [10 5]))
  ([size] 
     (let [[first-piece & stream] (repeatedly #(rand-nth (vals p/tetras)))]
       {:size size
        :board (apply b/empty-board size)
        :tetras stream
        :piece nil})))
