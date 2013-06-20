(ns io.rkn.tetra.system
  (:require [io.rkn.tetra.pieces :as p]
            [io.rkn.tetra.board :as b]
            [io.rkn.tetra.pieces :as p]
            [io.rkn.tetra :as tetra]
            [lanterna.screen :as s]))

(defn new-game
  [screen-type size]
  (let [piece-stream (repeatedly #(rand-nth (vals p/tetras)))]
    {:board (apply b/empty-board size)
     :tetras piece-stream
     :piece nil
     :screen (s/get-screen screen-type)
     :score 0
     :lines-cleared 0
     :uis [{:kind :menu}]}))

(defn -main [& args]
  (let [args (set args)
        screen-type (cond
                     (args ":swing") :swing
                     (args ":text")  :text
                     :else           :auto)
        game (new-game screen-type [20 10])
        screen (:screen game)]
    (try
      (s/start screen)
      (tetra/run-game game)
      (finally (s/stop screen)))))

