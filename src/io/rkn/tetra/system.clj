(ns io.rkn.tetra.system
  (:require [io.rkn.tetra.pieces :as p]
            [io.rkn.tetra.board :as b]
            [io.rkn.tetra.drawing :as d]
            [io.rkn.tetra.game :as g]
            [io.rkn.tetra.pieces :as p]
            [lanterna.screen :as s]))

(defn new-game
  [screen-type size] 
  (let [piece-stream (repeatedly #(rand-nth (vals p/tetras)))]
    {:board (apply b/empty-board size)
     :tetras piece-stream
     :piece nil
     :screen (s/get-screen screen-type)
     :uis [{:kind :menu}]}))

(defmulti process-input
  (fn [game] (-> game :uis last :kind)))

(defmethod process-input :menu [game]
  (let [input (:input game)
        game (dissoc game :input)]
    (condp = input
      :escape (assoc game :uis [])
      :enter (assoc game :uis [{:kind :play}])
      game)))

(defmethod process-input :play [game]
  (let [input (:input game)
        game-sans-input (dissoc game :input)
        new-state (condp = input
                    :escape (assoc game-sans-input :uis [])
                    :right (update-in game-sans-input [:piece :position :col] inc)
                    :left (update-in game-sans-input [:piece :position :col] dec)
                    :down (update-in game-sans-input [:piece :position :row] inc)
                    :up (update-in game-sans-input [:piece] g/rotate)
                    game)]
    (if (b/valid-posn? new-state)
      new-state
      game-sans-input)))

(defn get-input [game block?]
  (let [input-fn (if block?
                   s/get-key-blocking
                   s/get-key)]
    (assoc game :input (input-fn (:screen game)))))

(defmulti run-game
  (fn [game] (-> game :uis last :kind)))

(defmethod run-game :menu [game]
  (loop [{:keys [input uis] :as game} game]
    (when-not (empty? uis)
      (d/draw-game game)
      (if input
        (run-game (process-input game))
        (run-game (get-input game true))))))

(defn game-over? [game] false)

(defn refill-piece [game]
  (if (:piece game)
    game
    (g/select-tetra game)))

(defn tick-clock [game]
  (let [now (System/currentTimeMillis)
        before (:last-fall-time game)]
    ;; TODO: How can this be more well expressed?
    (if before
      (if (> (- now before) 1000)
        (-> game
            (assoc :fall-now? true)
            (assoc :last-fall-time now))
        game)
      (assoc game :last-fall-time now))))

(defmethod run-game :play [game]
  (loop [{:keys [input uis] :as game} game]
    (when-not (empty? uis)
      (d/draw-game game)
      (let [game (-> game
                     tick-clock)]
        (cond
         (game-over? game)
         (recur game) ;; TODO: Push game-over screen

         (nil? (:piece game))
         (recur (g/select-tetra game))

         (:fall-now? game)
         (recur (g/fall game))

         (:input game)
         (recur (process-input game))

         :else
         (do (Thread/sleep 10)
             (recur (get-input game false))))))))

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

