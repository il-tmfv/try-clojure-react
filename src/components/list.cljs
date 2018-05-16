(ns components.list)

(defn- render-list-item [{:keys [id name]}]
  [:div.list-item {:key id :style {:display "flex"}}
   [:div id]
   [:div name]
   ])

(defn render-list [data]
  [:div#list (map #(render-list-item %) data)])
