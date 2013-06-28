(ns ^:shared io.rkn.rouge.game
  (:require [io.pedestal.app.messages :as msg]
            [io.rkn.rouge.game.board :as b]
            [io.rkn.rouge.game.pieces :as p]
            [io.rkn.util.platform :as plat]))

(defn new-game [_ msg]
  (let [rows (get msg :rows 20)
        cols (get msg :cols 10)]
    {:board {:grid (b/empty-board rows cols)
             :piece nil}}))
(defn refresh-piece-if-missing [piece]
  (when-not piece
    [{msg/type :refresh-piece msg/topic [:game :board]}]))

(defn refresh-piece [board _]
  (let [next-piece (or (:next-piece board) (p/random-tetra))]
    (-> board
        (assoc-in [:piece] next-piece)
        (assoc-in [:next-piece] (p/random-tetra)))))

(defn land-piece
  ([board _] (land-piece board))
  ([board] (-> board
               (assoc :grid (b/graft-piece-to-grid board))
               (dissoc :piece))))

(defn lower-piece
  ([board _] (lower-piece board))
  ([board] (if (:piece board)
             (update-in board [:piece :position :row] inc))))

(defn about-to-collide?
  "Will a piece collide with the board's grid after the next step of gravity?"
  ([_ board] (about-to-collide? board))
  ([board] (-> board
               lower-piece
               b/collided?)))

