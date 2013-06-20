(ns io.rkn.tetra.game
  (:require [io.rkn.tetra.board :as b])
  (:require [io.rkn.tetra.pieces :as p]))

(defn game-over? [game]
  (not (b/valid-posn? game)))

(defn end-game [game]
  (assoc game :uis [{:kind :game-over}]))

(defn select-tetra [game]
  (let [[new-piece & others] (:tetras game)]
    (-> game
        (assoc-in [:piece] new-piece)
        (assoc-in [:tetras] others))))

(defn rotate-piece [game]
  (let [rotated (p/rotate game)]
    (if (b/valid-posn? rotated)
      rotated
      game)))

(defn fall [game]
  (let [potential-state (-> game
                            (dissoc :fall-now?)
                            (update-in [:piece :position :row] inc))]
    (if (b/valid-posn? potential-state)
      potential-state
      (if (b/collided? potential-state)
        (-> game
            b/graft-piece-to-board
            (dissoc :piece))
        game))))

(defn level
  "Return the game's level.

  = Quotient( cleared-lines / 10 ) + 1"
  [game] (+ 1 (quot (get game :lines-cleared 0) 10)))

(def points {1 100
             2 300
             3 500
             4 800})

(defn bump-score
  "Increase the score of a game by cleared-points * level."
  [game cleared]
  (let [to-add (* (get points cleared 0)
                  (level game))]
    (update-in game [:score] #(+ % to-add))))

(defn clear-and-score
  "Clear lines, adding score for any removed lines"
  [game]
  (let [{:keys [board]} game
        cleared-lines (count (filter b/full-row? board))
        cleared-game (b/clear-lines game)]
    (-> cleared-game
        (bump-score cleared-lines)
        (update-in [:lines-cleared] #(+ % cleared-lines)))))
