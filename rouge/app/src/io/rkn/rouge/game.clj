(ns ^:shared io.rkn.rouge.game
  (:require [io.pedestal.app.messages :as msg]
            [io.rkn.rouge.game.board :as b]
            [io.rkn.rouge.game.pieces :as p]
            [io.rkn.util.platform :as plat]
            [io.rkn.rouge.game.timers :as t]))

(defn game-over?
  "Is a game over? (i.e. piece is in an invalid position)"
  ([_ board] (game-over? board))
  ([board]
   (not (b/valid-posn? board))))

;; Transforms
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

(defn land-piece
  ([board _] (land-piece board))
  ([board]
   (-> board
               (assoc :grid (b/graft-piece-to-grid board))
               (dissoc :piece))))

(defn lower-piece
  ([board _] (lower-piece board))
  ([board]
   (if (:piece board)
             (update-in board [:piece :position :row] inc))))

;; Continues
(defn refresh-piece-if-missing [piece]
  (when-not piece
    [{msg/type :refresh-piece msg/topic [:game :board]}]))

(defn end-game-if-over [board]
  (when (game-over? board)
    [{msg/type :end-game msg/topic [:game :board]}]))

;; Derives
(defn about-to-collide?
  "Will a piece collide with the board's grid after the next step of gravity?"
  ([_ board] (about-to-collide? board))
  ([board] (-> board
               lower-piece
               b/collided?)))


(defn start-gravity-countdown [_ {:keys [game-over? landing?]}]
  (when-not (or game-over? landing?)
    (t/timeout 1000)))

(defn start-landing-countdown [_ {:keys [game-over? about-to-collide?]}]
  (when (and (not game-over?)
             about-to-collide?)
    (t/timeout 500)))

;; Effects
(defn affect-gravity [timeout-ch]
  (when timeout-ch
    [{msg/type :lower-piece msg/topic [:game :board] :timeout timeout-ch}]))

(defn affect-landing [timeout-ch]
  (when timeout-ch
    [{msg/type :land-piece msg/topic [:game :board] :timeout timeout-ch}]))
