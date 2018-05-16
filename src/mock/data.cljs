(ns mock.data)

(defn- generate-row [index]
  {
   :id   index
   :name (str "Test " index)
   })

(defn generate [count]
  (for [index (range count)]
    (generate-row index)))
