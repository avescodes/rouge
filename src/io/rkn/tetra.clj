(ns io.rkn.tetra
  (:require [io.rkn.tetra.board :as b]
            [io.rkn.tetra.drawing :as d]
            [io.rkn.tetra.game :as g]
            [lanterna.screen :as s]))

(def last-ui #(-> % :uis last :kind))

(defmulti process-input last-ui)
(defmulti run-game last-ui)

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
                    :up (g/rotate-piece game-sans-input)
                    game-sans-input)]
    (if (b/valid-posn? new-state)
      new-state
      game-sans-input)))

(defmethod process-input :game-over [game]
  (let [input (:input game)
        game (dissoc game :input)]
    (condp = input
      :escape (assoc game :uis [])
      :enter (assoc game :uis [{:kind :play}])
      game)))

(defn get-input
  "Gather input from the game's screen. block? indicates whether the function
  should wait for input or return nil immediately."
  [game block?]
  (let [input-fn (if block?
                   s/get-key-blocking
                   s/get-key)]
    (assoc game :input (input-fn (:screen game)))))

(defmethod run-game :menu [game]
  (loop [{:keys [input uis] :as game} game]
    (when-not (empty? uis)
      (d/draw-game game)
      (if input
        (run-game (process-input game))
        (run-game (get-input game true))))))

(defn tick-clock
  "'Tick' the game's clock, checking if more than 1000s has occured since the
  last time the board fell. Sets :fall-now? state to true if it has."
  [game]
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
  (loop [{:keys [input uis callback-fn] :as game} game]
    (when-not (empty? uis)
      (d/draw-game game)
      (if callback-fn
        (callback-fn game))
      (let [game (-> game
                     tick-clock)]
        (cond
         (g/game-over? game)
         (recur (g/end-game game))

         (nil? (:piece game))
         (recur (g/select-tetra game))

         (:fall-now? game)
         (recur (g/fall game))

         (b/clearable? game)
         (recur (g/clear-and-score game))

         (:input game)
         (recur (process-input game))

         :else
         (do (Thread/sleep 10)
             (recur (get-input game false))))))))

(defmethod run-game :game-over [game]
  (loop [{:keys [input uis] :as game} game]
    (when-not (empty? uis)
      (d/draw-game game)
      (if input
        (run-game (process-input game))
        (run-game (get-input game true))))))
 
