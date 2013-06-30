(ns io.rkn.rouge.simulated.start
  (:require [io.pedestal.app.protocols :as p]
            [io.pedestal.app :as app]
            [io.pedestal.app.messages :as msg]
            [io.pedestal.app.render.push.handlers.automatic :as d]
            [io.rkn.rouge.start :as start]
            [io.rkn.rouge.behavior :as behavior]
            [io.rkn.rouge.services :as services]
            [io.rkn.util.platform :as plat]
            [io.pedestal.app-tools.tooling :as tooling]))

(defn put-start-game-messages [input]
  (p/put-message input {msg/type :new-game msg/topic [:game] :rows 8 :cols 10}))

(defn debug-emitter [_] [[:transform-enable [:game] :land-piece [{msg/topic [:game :board]}]]
                         [:transform-enable [:game] :lower-piece [{msg/topic [:game :board]}]]])

(def debugging-behavior
  {:emit [{:init debug-emitter}]})

(defn ^:export main []
  (let [behavior (merge-with concat behavior/rouge-app debugging-behavior)
        {:keys [app] :as system} (start/create-app d/data-renderer-config behavior)]
    (app/consume-effects app services/services-fn)
    (put-start-game-messages (:input app))
    system))

