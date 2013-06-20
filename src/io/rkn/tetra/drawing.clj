(ns io.rkn.tetra.drawing
  (:require [lanterna.screen :as s]
            [io.rkn.tetra.game :as g]
            [io.rkn.tetra.board :as b]))

(defn char-for [point]
  (if (= 0 point)
    "."
    " "))

(def palette {0 {:fg :white :bg :black}
              1 {:bg :yellow}
              2 {:bg :cyan}
              3 {:bg :magenta}
              4 {:bg :blue}
              5 {:bg :white :fg :black}
              6 {:bg :red}
              7 {:bg :green}})

(defn clear-screen [screen]
  (let [blank (apply str (repeat 80 \space))]
    (doseq [row (range 24)]
      (s/put-string screen 0 row blank))))

(defn with-indices [coll]
  (map #(vec [%1 %2]) (range) coll))

(defmulti draw-ui (fn [ui game] (:kind ui)))

(defmethod draw-ui :menu [ui game]
  (let [screen (:screen game)]
    (s/put-string screen 0 0 "Welcome to Tetra, game of the future!")
    (s/put-string screen 0 1 "Press <enter> to play")
    (s/put-string screen 0 2 "Press <escape> to quit")))

(defn draw-score [game]
  (let [[_ board-rhs] (b/sizeb game)
        x (+ 2 board-rhs)
        y 1]
    (s/put-string (:screen game)
                  x y
                  (str "Score: " (:score game)))))
(defn draw-level [game]
  (let [[_ board-rhs] (b/sizeb game)
        x (+ 2 board-rhs)
        y 2]
    (s/put-string (:screen game)
                  x y
                  (str "Level: " (g/level game)))))

(defmethod draw-ui :play [ui game]
  (let [drawable-game (b/graft-piece-to-board game)
        [_ board-width] (b/sizeb drawable-game)]
    ;; Iterate over every coords of game
    (doseq [[y row] (with-indices (:board drawable-game))
            [x point] (with-indices row)]
      (s/put-string (:screen drawable-game)
                    x y
                    (char-for point)
                    (get palette point {})))
    (draw-score drawable-game)
    (draw-level drawable-game)))

(defn draw-game [game]
  (let [screen (:screen game)]
    (clear-screen screen)
    (doseq [ui (:uis game)]
      (draw-ui ui game))
    (s/move-cursor screen 0 23)
    (s/redraw screen)))

(defmethod draw-ui :game-over [ui game]
  (let [screen (:screen game)]
    (s/put-string screen 0 0 "Game over!")
    (s/put-string screen 0 1 "Press <enter> to play again")
    (s/put-string screen 0 2 "Press <escape> to quit")))
