(ns io.rkn.util.platform)

(defn log [& more]
  (binding [*print-fn* #(.log js/console %)]
    (apply prn more)))
