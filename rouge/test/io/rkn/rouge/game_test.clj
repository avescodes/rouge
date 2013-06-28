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

(defn new-game-msg [size]
  {msg/type :new-game msg/topic [:game] :rows size :cols size})
(def refresh-piece-msg {msg/type :refresh-piece msg/topic [:game :board]})
;; Test a transform function

(deftest test-new-game-transform
  (is (= (new-game {} (new-game-msg 1))
         {:board  {:grid [[0]], :piece nil}})))

;; Build an application, send a message to a transform and check the transform
;; state

(deftest test-app-state
  (let [app (app/build be/example-app)]
    (is (vector?
         (test/run-sync! app [(new-game-msg 4)] :begin :default)))
    (is (= (-> app :state deref :data-model :game :board :grid) [[0 0 0 0]
                                                                 [0 0 0 0]
                                                                 [0 0 0 0]
                                                                 [0 0 0 0]]))
    (is (-> app :state deref :data-model :game :board :piece) "refresh-piece occurs automatically")))

(deftest land-piece-test
  (is (= {:grid [[2]]}
        (land-piece {:grid [[0]]
                     :piece {:shape [[1]]
                             :color 2
                             :position {:row 0 :col 0}}}))))

(deftest landing-piece-refreshes-piece-test
  (let [app (app/build be/example-app)]
    (test/run-sync! app [(new-game-msg 4)
                         {msg/topic [:game :board] msg/type :land-piece}])
    (testing "after land-piece"
      (is (-> app :state deref :data-model :game :board :piece) "refresh-piece occurs automatically"))))

(deftest about-to-collide?-test
  (let [board {:grid [[0]
                      [0]]
               :piece {:shape [[1]]}}
        touching {:row 1 :col 0}
        not-touching {:row 0 :col 0}]
    (is (about-to-collide? (assoc-in board [:piece :position] touching)))
    (is (not (about-to-collide? (assoc-in board [:piece :position] not-touching))))))

(comment
  (deftest level-test
    (are [expected game]
         (is (= expected (level game)))
         1 {:lines-cleared 0}
         1 {:lines-cleared 9}
         2 {:lines-cleared 10}))

  (deftest bump-score-test
    (are [original-game cleared expected]
         (is (= expected (bump-score original-game cleared)))
         {:score 0 :lines-cleared 0} 4 {:score 800 :lines-cleared 4}
         {:score 1000 :lines-cleared 10} 2 {:score 1600 :lines-cleared 12}))
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

