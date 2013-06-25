(ns io.rkn.rouge.simulated.start
  (:require [io.pedestal.app.protocols :as p]
            [io.pedestal.app.messages :as msg]
            [io.pedestal.app.render.push.handlers.automatic :as d]
            [io.rkn.rouge.start :as start]
            [io.pedestal.app-tools.tooling :as tooling]))

(defn put-start-game-messages [input]
  (p/put-message input {msg/type :new-game msg/topic [:game] :rows 5 :cols 10}))

(defn ^:export main []
  (let [{:keys [app] :as system} (start/create-app d/data-renderer-config)]
    (put-start-game-messages (:input app))
    system))
