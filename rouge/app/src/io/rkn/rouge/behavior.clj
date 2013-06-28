(ns ^:shared io.rkn.rouge.behavior
    (:require [clojure.string :as string]
              [io.rkn.rouge.game :as g]
              [io.rkn.rouge.game.board :as b]
              [io.rkn.rouge.game.timers :as timers]
              [io.pedestal.app.messages :as msg]))

(def example-app
  {:version 2
   :transform [[:new-game [:game] g/new-game]
               [:refresh-piece [:game :board] g/refresh-piece]
               [:lower-piece [:game :board] g/lower-piece]
               [:land-piece [:game :board] g/land-piece]]
   :derive [[#{[:game :board]} [:game :display :board] b/graft-piece-to-grid :single-val]
            [#{[:game :board]} [:game :board :about-to-collide?] g/about-to-collide? :single-val]
            [#{[:game :board]} [:game :board :game-over?] g/game-over? :single-val]
            [{[:game :board :piece :position :row] :row [:game :board :about-to-collide?] :landing?} [:game :gravity-channel] g/start-gravity-countdown :map]
            [#{[:game :board :about-to-collide?]} [:game :landing-channel] g/start-landing-countdown :single-val]]
   :effect #{[#{[:game :gravity-channel]} g/affect-gravity :single-val]
             [#{[:game :landing-channel]} g/affect-landing :single-val]}
   :continue [[#{[:game :board :piece]} g/refresh-piece-if-missing :single-val]]})

