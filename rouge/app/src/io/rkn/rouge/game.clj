(ns ^:shared io.rkn.rouge.game
  (:require [io.pedestal.app.messages :as msg]
            [io.rkn.rouge.game.board :as b]
            [io.rkn.rouge.game.pieces :as p]
            [io.rkn.util.platform :as plat]
            [io.rkn.rouge.game.timers :as t]))

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

(defn game-over?
  "Is a game over? (i.e. piece is in an invalid position)"
  ([_ board] (game-over? board))
  ([board] (not (b/valid-posn? board))))

(defn start-gravity-countdown [_ {:keys [landing?]}]
  (when-not landing?
    (t/timeout 1000)))

(defn affect-gravity [channel]
  (when channel
    [{msg/type :lower-piece msg/topic [:game :board] :timeout channel}]))

(defn start-landing-countdown [_ start-lock?]
  (when start-lock?
    (t/timeout 500)))

(defn affect-landing [channel]
  (when channel
    [{msg/type :land-piece msg/topic [:game :board] :timeout channel}]))
