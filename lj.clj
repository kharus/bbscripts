(require '[babashka.http-client :as http]
         '[babashka.json :as json]
         '[lambdaisland.uri :refer [uri]]
         '[babashka.fs :as fs]
         '[selmer.parser :as selmer]
         '[babashka.pods :as pods]
         '[meander.epsilon :as m])
(pods/load-pod 'retrogradeorbit/bootleg "0.1.9")
(pods/load-pod 'com.github.jackdbd/jsoup "0.4.0")

; https://ailev.livejournal.com/1758321.html

(require
 '[pod.retrogradeorbit.bootleg.utils :as bootleg]
 '[pod.retrogradeorbit.hickory.select :as s]
 '[pod.jackdbd.jsoup :as jsoup])

(def page-type-map
  {"HEADER" :header
   "TEXT" :text
   "TEST" :test})

(defn hickory-html [raw-html]
  (->> (bootleg/convert-to raw-html :hickory-seq)
       (filter #(= (:tag %) :html))
       first))

(comment
  (def projector-raw
    (:body (http/get "https://ailev.livejournal.com/1758321.html")))
  (hickory-html projector-raw)
  (def html-article
    (first (jsoup/select (slurp "quq.html") "article.entry-content")))
  
  (hickory-html (:outer-html html-article))
  
  (bootleg/convert-to (:outer-html html-article) :hickory-seq))