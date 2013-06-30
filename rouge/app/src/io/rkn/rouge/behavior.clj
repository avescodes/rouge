(ns ^:shared io.rkn.rouge.behavior
    (:require [clojure.string :as string]
              [io.pedestal.app.messages :as msg]
              [io.pedestal.app :as app]
              [io.rkn.rouge.game :as g]
              [io.rkn.rouge.game.board :as b]))

(defn handle-input [_]
  [#_[:transform-enable [:game :board] :handle-input [{msg/topic [:game :board]}]]])

(def rouge-app
  {:version 2
   :transform [[:new-game [:game] g/new-game]
               [:refresh-piece [:game :board] g/refresh-piece]
               [:lower-piece [:game :board] g/lower-piece]
               [:land-piece [:game :board] g/land-piece]]
   :continue [[#{[:game :board :piece]} g/refresh-piece-if-missing :single-val]]
   :derive [[#{[:game :board]} [:game :display :board] b/graft-piece-to-grid :single-val]
            [#{[:game :board]} [:game :board :about-to-collide?] g/about-to-collide? :single-val]
            [#{[:game :board]} [:game :board :game-over?] g/game-over? :single-val]
            [{[:game :board :piece :position :row] :row [:game :board :about-to-collide?] :landing? [:game :board :game-over?] :game-over?} [:game :board :gravity-channel] g/start-gravity-countdown :map]
            [{[:game :board :about-to-collide?] :about-to-collide? [:game :board :game-over?] :game-over?} [:game :board :landing-channel] g/start-landing-countdown :map]]
   :effect #{[#{[:game :board :gravity-channel]} g/affect-gravity :single-val]
             [#{[:game :board :landing-channel]} g/affect-landing :single-val]}
   :emit [{:init handle-input}
          [#{[:*]} (app/default-emitter :game)]]})

