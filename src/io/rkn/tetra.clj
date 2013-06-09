(ns io.rkn.tetra
  (:require [io.rkn.tetra.board :as b]))

(defn draw [game]
  (let [palette {0 "."
                 1 "Y"
                 2 "C"
                 3 "P"
                 4 "B"
                 5 "O"
                 6 "R"
                 7 "G"}
        filled-game (b/graft-piece-to-board game)]
    (->> (:board filled-game)
         (b/mapb palette)
         (mapv #(apply str %))
         (interpose "\n")
         (apply str))))
