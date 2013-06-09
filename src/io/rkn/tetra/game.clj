(io.rkn.tetra.game
 (:require [io.rkn.tetra.board :as b]))

(defn select-tetra [game]
  (let [[new-piece & others] (:tetras game)]
    (-> game
        (assoc-in [:piece] new-piece)
        (assoc-in [:tetras] others))))

(defn fall [game]
  (let [potential-state (update-in game [:piece :position :row] inc)]
    (if (b/valid-posn? potential-state)
      potential-state
      (if (b/collided? potential-state)
        (b/graft-piece-to-board game)
        game))))
