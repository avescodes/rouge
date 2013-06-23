(ns ^:shared io.rkn.rouge.game
  (:require [io.rkn.rouge.game.board :as b]))

(defn new-game [_ msg]
  (let [rows (get options :rows 20)
        cols (get options :cols 10)]
    {:board {:landed (empty-board rows cols)
             :piece 1}}))
