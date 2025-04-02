(ns lg-epub.lj
  (:require [babashka.http-client :as http]
    [babashka.json :as json]
    [lambdaisland.uri :refer [uri]]
    [babashka.fs :as fs]
    [selmer.parser :as selmer]
    [babashka.pods :as pods]
    [meander.epsilon :as m]))

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
  
  (bootleg/convert-to (:outer-html html-article) :hickory-seq)
  (spit "article.html" (:outer-html html-article)))


(selmer/set-resource-path! "/home/ruslan/code/bbscripts/templates")

(spit "content.opf"
      (selmer/render-file "content.opf"
                          {:title "Попсовости обучения в нашем клубе не будет, давать \"короткие концерты по справочнику\" не согласны" 
                           :uuid (java.util.UUID/randomUUID)
                           :now (.format java.time.format.DateTimeFormatter/ISO_INSTANT (java.time.Instant/now))

                           }))

(fs/zip "Попсовости обучения в нашем клубе не будет.epub"
     "Попсовости обучения в нашем клубе не будет, давать \"короткие концерты по справочнику\" не согласны"
        {:root "Попсовости обучения в нашем клубе не будет, давать \"короткие концерты по справочнику\" не согласны"})


(spit "Section0001.xhtml"
      (selmer/render-file "Section0001.xhtml"
                          {:title "Попсовости обучения в нашем клубе не будет, давать \"короткие концерты по справочнику\" не согласны"
                           :article (:outer-html html-article)}))

(first (jsoup/select (slurp "quq.html") "meta[property=og:title]"))

(fs/copy-tree "epub-template" "Попсовости обучения в нашем клубе не будет, давать \"короткие концерты по справочнику\" не согласны")

