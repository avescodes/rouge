(ns io.rkn.rouge.game-test
  (:require [io.pedestal.app :as app]
            [io.pedestal.app.protocols :as p]
            [io.pedestal.app.tree :as tree]
            [io.pedestal.app.messages :as msg]
            [io.pedestal.app.render :as render]
            [io.pedestal.app.util.test :as test]
            [io.rkn.rouge.behavior :as be])
  (:use clojure.test
        io.rkn.rouge.game
        [io.pedestal.app.query :only [q]]))

(def new-game-msg {msg/type :new-game msg/topic [:game] :rows 1 :cols 1})
;; Test a transform function

(deftest test-new-game-transform
  (is (= (new-game {} new-game-msg)
         {:board  {:landed  [[0]], :piece nil}})))

;; Build an application, send a message to a transform and check the transform
;; state

(deftest test-app-state
  (let [app (app/build be/example-app)]
    (app/begin app)
    (is (vector?
         (test/run-sync! app [new-game-msg])))
    (is (= (-> app :state deref :data-model :game :board :landed) [[0]]))))

(comment
  ;; Use io.pedestal.app.query to query the current application model

  (deftest test-query-ui
    (let [app (app/build be/example-app)
          app-model (render/consume-app-model app (constantly nil))]
      (app/begin app)
      (is (test/run-sync! app [{msg/topic [:greeting] msg/type :set-value :value "x"}]))
      (is (= (q '[:find ?v
                  :where
                  [?n :t/path [:greeting]]
                  [?n :t/value ?v]]
                @app-model)
             [["x"]])))))

