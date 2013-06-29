(ns io.rkn.rouge.start
  (:require [io.pedestal.app.protocols :as p]
            [io.pedestal.app :as app]
            [io.pedestal.app.render.push :as push-render]
            [io.pedestal.app.render :as render]
            [io.pedestal.app.messages :as msg]
            [io.rkn.rouge.behavior :as behavior]
            [io.rkn.rouge.rendering :as rendering]))

(defn create-app
  ([render-config] (create-app render-config behavior/rouge-app))
  ([render-config behavior]
   (let [app (app/build behavior)
         render-fn (push-render/renderer "content" render-config render/log-fn)
         app-model (render/consume-app-model app render-fn)]
     (app/begin app)
     {:app app :app-model app-model})))

(defn ^:export main []
  (create-app (rendering/render-config)))
