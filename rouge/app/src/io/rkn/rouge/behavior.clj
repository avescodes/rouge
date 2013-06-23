(ns ^:shared io.rkn.rouge.behavior
    (:require [clojure.string :as string]
              [io.rkn.rouge.game :as g]
              [io.pedestal.app.messages :as msg]))

(def example-app
  {:version 2
   :transform [[:new-game [:game] g/new-game]]
   })

