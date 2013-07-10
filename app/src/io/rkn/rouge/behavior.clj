(ns ^:shared io.rkn.rouge.behavior
    (:require [clojure.string :as string]
              [io.pedestal.app.messages :as msg]
              [io.pedestal.app :as app]
              [io.pedestal.app.dataflow :as d]
              [io.rkn.rouge.game :as g]
              [io.rkn.util.platform :as plat]
              [io.rkn.rouge.game.board :as b]))

(defn game-emitter [inputs]
  (if (-> (d/added-inputs inputs) (get [:game]))
    [[:transform-disable [:game] :new-game]
     [:transform-enable [:game] :left [{msg/type :player-input msg/topic [:game :board] :direction :left}]]
     [:transform-enable [:game] :right [{msg/type :player-input msg/topic [:game :board] :direction :right}]]
     [:transform-enable [:game] :up [{msg/type :player-input msg/topic [:game :board] :direction :up}]]
     [:transform-enable [:game] :down [{msg/type :player-input msg/topic [:game :board] :direction :down}]]]))

(defn initial-game [_]
  [[:transform-enable [:game] :new-game [{msg/topic [:game] :rows 20 :cols 10}]]])

(def rouge-app
  {:version 2
   :transform [[:new-game [:game] g/new-game]
               [:refresh-piece [:game :board] g/refresh-piece]
               [:lower-piece [:game :board] g/lower-piece]
               [:land-piece [:game :board] g/land-piece]
               [:clear-lines [:game :board] g/clear-lines]
               ;; Player transforms
               [:player-input [:game :board] g/player-input]]
   :continue [[#{[:game :board :piece]} g/refresh-piece-if-missing :single-val]
              [#{[:game :board :grid]} g/clear-lines-if-clearable :single-val]]
   :derive [[#{[:game :board :score]} [:game :display :score] (fn [_ x] x) :single-val]
            [#{[:game :board]} [:game :display :board] (fn [_ b] (select-keys b [:grid :piece])) :single-val]
            [#{[:game :board]} [:game :display :level] g/level :single-val]
            [#{[:game :board]} [:game :display :next-piece] g/next-piece-display :single-val]
            [#{[:game :board]} [:game :board :about-to-collide?] g/about-to-collide? :single-val]
            [#{[:game :board]} [:game :board :game-over?] g/game-over? :single-val]
            [{[:game :board :piece :position :row] :row [:game :board :about-to-collide?] :landing? [:game :board :game-over?] :game-over?} [:game :board :gravity-channel] g/start-gravity-countdown :map]
            [{[:game :board :about-to-collide?] :about-to-collide? [:game :board :game-over?] :game-over?} [:game :board :landing-channel] g/start-landing-countdown :map]]
   :effect #{[#{[:game :board :gravity-channel]} g/affect-gravity :single-val]
             [#{[:game :board :landing-channel]} g/affect-landing :single-val]}
   :emit [[#{[:game :display :board :*]
             [:game :display :level]
             [:game :display :score]
             [:game :display :next-piece]} (app/default-emitter [])]
          {:in #{[:game]} :fn game-emitter :init initial-game :mode :always}]})

