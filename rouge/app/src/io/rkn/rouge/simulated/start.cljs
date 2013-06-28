(ns io.rkn.rouge.simulated.start
  (:require [io.pedestal.app.protocols :as p]
            [io.pedestal.app :as app]
            [io.pedestal.app.messages :as msg]
            [io.pedestal.app.render.push.handlers.automatic :as d]
            [io.rkn.rouge.start :as start]
            [io.rkn.rouge.behavior :as behavior]
            [io.pedestal.app-tools.tooling :as tooling]))

(defn put-start-game-messages [input]
  (p/put-message input {msg/type :new-game msg/topic [:game] :rows 5 :cols 10}))

(defn debug-emitter [_] [[:transform-enable [:game] :lower-piece [{msg/topic [:game :board]}]]])

(def debugging-behavior
  {:emit [{:in #{[:game :board] [:game :piece]}
            :fn debug-emitter}
           {:in #{[:*]} :fn (app/default-emitter [])}]})

(defn ^:export main []
  (let [behavior (merge behavior/example-app debugging-behavior)
        {:keys [app] :as system} (start/create-app d/data-renderer-config behavior)]
    (put-start-game-messages (:input app))
    system))
