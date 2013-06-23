(ns ^:shared io.rkn.rouge.game.board)

;; Board creation
(def create-vector (comp vec repeat))

(defn empty-board
  ([rows cols fill] (create-vector rows (create-vector cols fill)))
  ([rows cols] (empty-board rows cols 0)))

