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
        y 8]
    (s/put-string (:screen game)
                  x y
                  (str "Score: " (:score game)))))
(defn draw-level [game]
  (let [[_ board-rhs] (b/sizeb game)
        x (+ 2 board-rhs)
        y 9]
    (s/put-string (:screen game)
                  x y
                  (str "Level: " (g/level game)))))

(defn draw-board [screen top-left board]
  (let [[top left] top-left]
    (doseq [[y row] (with-indices board)
            [x point] (with-indices row)]
      (s/put-string screen
                    (+ left x) (+ top y)
                    (char-for point)
                    (get palette point {})))))

(defn draw-next-piece [game]
  (let [s (:screen game)
        [_ board-rhs] (b/sizeb game)
        x (+ 2 board-rhs)
        y 1]
    (s/put-string s x (+ y 0) (str "XXXXXX"))
    (s/put-string s x (+ y 1) (str "X    X"))
    (s/put-string s x (+ y 2) (str "X    X"))
    (s/put-string s x (+ y 3) (str "X    X"))
    (s/put-string s x (+ y 4) (str "X    X"))
    (s/put-string s x (+ y 5) (str "XXXXXX"))
    (let [fake-board (b/empty-board 4 4)
          next-piece (-> game :tetras first)
          top-left [(+ y 1) (+ x 1)]
          {next-piece-board :board} (b/graft-piece-to-board {:piece next-piece
                                                             :board fake-board})]
      (draw-board s top-left next-piece-board))))



(def origin [0 0])
(defmethod draw-ui :play [ui game]
  (let [drawable-game (b/graft-piece-to-board game)
        [_ board-width] (b/sizeb drawable-game)]
    ;; Iterate over every coords of game
    (draw-board (:screen game) origin (:board drawable-game))
    (draw-score drawable-game)
    (draw-level drawable-game)
    (draw-next-piece drawable-game)))

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
