(ns day8.re-frame.trace.devtools-formatters
  (:require [clojure.string :as str]))

(defn css-munge
  [string]
  (str/replace string #"\.|/" "-"))

(defn namespace-css
  [classname]
  (str "re-frame-trace--" classname))

(defn type-string
  [obj]
  (cond
    (number? obj)    "number"
    (boolean? obj)   "boolean"
    (string? obj)    "string"
    (nil? obj)       "nil"
    (keyword? obj)   "keyword"
    (symbol? obj)    "symbol"
    :else (pr-str (type obj))))

(defn view
  [data]
  (if (coll? data)
    [:div  {:class (str (namespace-css "collection") " " (namespace-css (css-munge (type-string data))))}]
    [:span {:class (str (namespace-css "primative") " " (namespace-css (css-munge (type-string data))))} (str data)]))

(defn crawl
  [data]
  (if (coll? data)
    (into (view data) (mapv crawl data))
    (view data)))

(defn header [obj]
  #js ["div" {} (pr-str obj)])

(defn has-body []
  true)

(defn body [data]
  (clj->js (crawl data)))

(defn inject! []
  (set! (.-devtoolsFormatters js/window) #js [ #js {:header  header
                                                    :hasBody has-body
                                                    :body    body}]))
