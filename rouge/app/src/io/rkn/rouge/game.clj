(ns ^:shared io.rkn.rouge.game
  (:require [io.pedestal.app.messages :as msg]
            [io.rkn.rouge.game.board :as b]
            [io.rkn.rouge.game.pieces :as p]))

(defn new-game [_ msg]
  (let [rows (get msg :rows 20)
        cols (get msg :cols 10)]
    {:board {:grid (b/empty-board rows cols)
             :piece nil}}))

(defn refresh-piece [board _]
  (let [next-piece (or (:next-piece board) (p/random-tetra))]
    (-> board
        (assoc-in [:piece] next-piece)
        (assoc-in [:next-piece] (p/random-tetra)))))

(defn refresh-piece-if-missing [piece]
  (when-not piece
    [{msg/type :refresh-piece msg/topic [:game :board]}]))
