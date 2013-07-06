(ns io.rkn.rouge.game-test
  (:require [io.pedestal.app :as app]
            [io.pedestal.app.protocols :as p]
            [io.pedestal.app.tree :as tree]
            [io.pedestal.app.messages :as msg]
            [io.pedestal.app.render :as render]
            [io.pedestal.app.util.test :as test]
            [io.rkn.rouge.behavior :as be]
            [io.rkn.rouge.game.grid :as grid]
            [io.rkn.rouge.game.timers :as timers]
            [clojure.core.async :as as])
  (:use clojure.test
        io.rkn.rouge.game
        [io.pedestal.app.query :only [q]]))

(defn new-game-msg [size]
  {msg/type :new-game msg/topic [:game] :rows size :cols size})
;; Test a transform function

(deftest test-new-game-transform
  (is (= (new-game {} (new-game-msg 1))
         {:board  {:grid [[0]], :piece nil
                   :score 0, :lines-cleared 0}})))

;; Build an application, send a message to a transform and check the transform
;; state

(deftest test-app-state
  (let [app (app/build be/rouge-app)]
    (is (vector?
          (test/run-sync! app [(new-game-msg 10)] :begin :default)))
    (is (= (-> app :state deref :data-model :game :board :grid) (grid/empty-grid 10 10)))
    (is (-> app :state deref :data-model :game :board :piece) "refresh-piece occurs automatically")))

(deftest land-piece-test
  (is (= {:grid [[2]]}
         (land-piece {:grid [[0]]
                      :piece {:shape [[1]]
                              :color 2
                              :position {:row 0 :col 0}}}))))

(deftest landing-piece-refreshes-piece-test
  (let [app (app/build be/rouge-app)]
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
    (is (not (about-to-collide? (assoc-in board [:piece :position] not-touching))))
    (is (not (about-to-collide? (dissoc board :piece))))))

(deftest level-test
  (are [expected game]
       (is (= expected (level game)))
       1 {:lines-cleared 0}
       1 {:lines-cleared 9}
       2 {:lines-cleared 10}))

(deftest bump-score-test
  (are [board cleared expected]
       (is (= expected (bump-score board cleared)))
       {:score 0 :lines-cleared 1} 4 {:score 800 :lines-cleared 1}
       {:score 1000 :lines-cleared 10} 2 {:score 1600 :lines-cleared 10}))

(deftest clear-lines-test
  (is (= {:grid [[0 0]
                 [0 0]]
          :score 300
          :lines-cleared 2}
         (clear-lines {:grid [[1 1]
                              [1 1]]
                       :score 0
                       :lines-cleared 0}
                      {}))))
