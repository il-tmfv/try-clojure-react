(ns try-clojure-react.core
  (:require [reagent.core :as r]
            [mock.data :as mock]
            [components.hello :as hello]
            [components.list :as list-component]
            ))

(enable-console-print!)

;; define your app data so that it doesn't get over-written on reload

(defonce app-state
         (r/atom {:items-count 0}))

(defonce items-count
         (r/cursor app-state [:items-count]))

(defn add-item []
  (swap! items-count #(if (>= % 10) 10 (inc %))))

(defn remove-item []
  (swap! items-count #(if (<= % 0) 0 (dec %))))

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
