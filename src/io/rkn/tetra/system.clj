(ns io.rkn.tetra.system
  (:require [io.rkn.tetra.pieces :as p]
            [io.rkn.tetra.board :as b]
            [io.rkn.tetra.drawing :as d]
            [io.rkn.tetra.game :as g]
            [lanterna.screen :as s]))

(defrecord UI [kind])

(defn new-game
  [screen-type size] 
  (let [piece-stream (repeatedly #(rand-nth (vals p/tetras)))]
    {:board (apply b/empty-board size)
     :tetras piece-stream
     :piece nil
     :screen (s/get-screen screen-type)
     :uis [{:kind :play}]}))

(defmulti process-input
  (fn [game input] (-> game :uis last :kind)))

(defmethod process-input :play [game input]
  (assoc game :uis []))

(defn get-input [game block?]
  (let [input-fn (if block?
                   s/get-key-blocking
                   s/get-key)]
    (assoc game :input (input-fn (:screen game)))))

(defmulti run-game
  (fn [game] (-> game :uis last :kind)))

(defn refill-piece [game]
  (if (:piece game)
    game
    (g/select-tetra game)))

(defn tick-clock [game]
  (let [now (System/currentTimeMillis)
        before (:last-fall-time game)]
    ;; TODO: How can this be more well expressed?
    (if before
      (if (> (- now before) 500)
        (-> game
            (assoc :fall-now true)
            (assoc :last-fall-time now))
        game)
      (assoc game :last-fall-time now))))

(defn maybe-fall [game]
  (if (:fall-now game)
    (-> game
        g/fall
        (dissoc :fall-now))
    game))

(defmethod run-game :play [game]
  (loop [{:keys [input uis] :as game} game]
    (when-not (empty? uis)
      (let [game (-> game
                     refill-piece
                     tick-clock
                     maybe-fall)]
        (d/draw-game game)
        (Thread/sleep 10)
        (recur game)))))

(defn -main [& args]
  (let [args (set args)
        screen-type (cond
                     (args ":swing") :swing
                     (args ":text")  :text
                     :else           :auto)
        game (new-game screen-type [20 10])
        screen (:screen game)]
    (try
      (s/start screen)
      (run-game game)
      (finally (s/stop screen)))))

