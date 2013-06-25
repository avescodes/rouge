(ns ^:shared io.rkn.rouge.behavior
    (:require [clojure.string :as string]
              [io.rkn.rouge.game :as g]
              [io.rkn.rouge.game.board :as b]
              [io.pedestal.app.messages :as msg]))

(def example-app
  {:version 2
   :transform [[:new-game [:game] g/new-game]
               [:refresh-piece [:game :board] g/refresh-piece]]
   :derive [[#{[:game :board]} [:game :display :board] b/graft-piece-to-grid :single-val]]
   :continue [[#{[:game :board :piece]} g/refresh-piece-if-missing :single-val]]
   })

