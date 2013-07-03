(ns io.rkn.rouge.html-templates
  (:use [io.pedestal.app.templates :only [tfn dtfn tnodes]]))

(defmacro rouge-templates
  []
  {:rouge-page (dtfn (tnodes "rouge.html" "hello") #{:id})})
