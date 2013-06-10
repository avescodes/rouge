(ns io.rkn.tetra.game
 (:require [io.rkn.tetra.board :as b]))

(defn select-tetra [game]
  (let [[new-piece & others] (:tetras game)]
    (-> game
        (assoc-in [:piece] new-piece)
        (assoc-in [:tetras] others))))

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
