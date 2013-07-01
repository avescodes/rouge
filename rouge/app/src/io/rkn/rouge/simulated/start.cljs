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

(defn ^:export main []
  (let [{:keys [app] :as system} (start/create-app d/data-renderer-config)]
    (app/consume-effects app services/services-fn)
    system))

