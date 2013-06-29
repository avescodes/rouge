(ns io.rkn.rouge.game.timers
  (:require [io.pedestal.app.protocols :as p]
            [io.pedestal.app :as app]
            [io.pedestal.app.messages :as msg]
            [clojure.core.async :refer [<! go] :as as]))

(defn timeout [ms] (as/timeout ms))

(defn delayed-put [msg input-queue]
  (let [ch (:timeout msg)]
    (go
      (<! ch)
      (p/put-message input-queue msg))))
