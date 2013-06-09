(ns io.rkn.tetra.system
  (:require [io.rkn.tetra.pieces :as p]
            [io.rkn.tetra.board :as b]
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

(defn clear-screen [screen]
  (let [blank (apply str (repeat 80 \space))]
    (doseq [row (range 24)]
      (s/put-string screen 0 row blank))))

(defmulti draw-ui (fn [ui game] (:kind ui)))

(defmethod draw-ui :play [ui game]
  (s/put-string (:screen game) 0 0 "Congratulations, you win!")
  (s/put-string (:screen game) 0 1 "Press escape to exit, anything else to restart."))

(defn draw-game [game]
  (let [screen (:screen game)]
    (clear-screen screen)
    (doseq [ui (:uis game)]
      (draw-ui ui game))
    (s/redraw screen)))

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

(defmethod run-game :play [game]
    (loop [{:keys [input uis] :as game} game]
    (when-not (empty? uis)
      (draw-game game)
      (if (nil? input)
        (recur (get-input game true))
        (recur (process-input (dissoc game :input) input))))))

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

