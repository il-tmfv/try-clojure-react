(ns try-clojure-react.core
  (:require [reagent.core :as r]
            [mock.data :as mock]
            [components.hello :as hello]
            [components.list :as list-component]
            [cljs.core.async :refer [chan >! <!]]
            )
  (:require-macros [cljs.core.async :refer [go go-loop]]))

(enable-console-print!)

;; define your app data so that it doesn't get over-written on reload
(defonce app-state
         (r/atom {:items-count 0}))

(defonce items-count
         (r/cursor app-state [:items-count]))

(defonce items-count-chan (chan 10))

(defn add-item []
  (swap! items-count #(if (>= % 10) 10 (inc %))))

(defn remove-item []
  (swap! items-count #(if (<= % 0) 0 (dec %))))

(add-watch items-count :count-watcher
           (fn [_k _r o n]
             (go
               (>! items-count-chan n)
               (println
                 (str "New count: " n ", was: " o)))))

(go-loop []
         (print "Got new count from a chan: " (<! items-count-chan))
         (recur))

(defn on-js-reload [])
;; optionally touch your app-state to force rerendering depending on
;; your application
;; (swap! app-state update-in [:__figwheel_counter] inc)

(defn app []
  [:div#root
   [hello/render-hello]
   [:h3 "List:"]
   [:button {:on-click add-item} "Add one"]
   [:button {:on-click remove-item} "Remove one"]
   [list-component/render-list (mock/generate @items-count)]
   ])

(r/render [app]
          (.getElementById js/document "app"))
