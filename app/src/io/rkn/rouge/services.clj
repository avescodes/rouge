(ns ^:shared io.rkn.rouge.services
  (:require [io.rkn.util.platform :as plat]
            [io.rkn.rouge.game.timers :as t]))

(defn services-fn  [msg input-queue]
  (plat/log (str "Sent off message: " msg))
  (t/delayed-put msg input-queue))
