(ns try-clojure-react.core
  (:require [reagent.core :as r]
            [mock.data :as mock]
            [components.hello :as hello]
            [components.list :as list-component]
            [cljs.core.async :refer [chan >! <! timeout]]
            )
  (:require-macros [cljs.core.async :refer [go go-loop]]))

(enable-console-print!)

;; define your app data so that it doesn't get over-written on reload
(defonce app-state
         (r/atom {:items-count 0 :items [] :loading false}))

(defonce items-count
         (r/cursor app-state [:items-count]))

(defonce items
         (r/cursor app-state [:items]))

(defonce loading
         (r/cursor app-state [:loading]))

(defonce items-count-chan (chan 10))

(defonce items-chan (chan 10))

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
         (let [new-count (<! items-count-chan)]
           (print "Got new count from a chan: " new-count)
           (swap! loading #(identity true))
           (<! (timeout 1000))
           (>! items-chan (mock/generate new-count))
           (swap! loading #(identity false))
           (recur)))

(go-loop []
         (let [new-items (<! items-chan)]
           (swap! items #(identity new-items))
           (recur)))

(defn on-js-reload [])
;; optionally touch your app-state to force rerendering depending on
;; your application
;; (swap! app-state update-in [:__figwheel_counter] inc)

(defn app []
  [:div#root
   [hello/render-hello]
   [:h3 "List:"]
   (when @loading [:h3 "Loading"])
   [:button {:on-click add-item} "Add one"]
   [:button {:on-click remove-item} "Remove one"]
   [list-component/render-list @items]
   ])

(r/render [app]
          (.getElementById js/document "app"))
