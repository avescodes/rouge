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
