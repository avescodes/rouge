(ns io.rkn.rouge.game.timers
  (:require [io.pedestal.app.protocols :as p]
            [io.pedestal.app :as app]
            [io.pedestal.app.messages :as msg]
            [cljs.core.async :refer [chan close!]])
  (:require-macros
    [cljs.core.async.macros :as m :refer  [go]]))

(defn timeout [ms]
  (let [c (chan)]
    (js/setTimeout (fn [] (close! c)) ms)
    c))

(defn delayed-put [msg input-queue]
  (let [ch (:timeout msg)]
    (go
      (<! ch)
      (p/put-message input-queue msg))))
