(ns lg-epub.lj
  (:require
   [babashka.cli :as cli]
   [babashka.fs :as fs]
   [babashka.http-client :as http]
   [selmer.parser :as selmer]
   [pod.retrogradeorbit.bootleg.utils :as bootleg]
   [pod.jackdbd.jsoup :as jsoup]))

(defn hickory-html [raw-html]
  (->> (bootleg/convert-to raw-html :hickory-seq)
       (filter #(= (:tag %) :html))
       first))

(selmer/set-resource-path! "/home/ruslan/src/bbscripts/templates")

(comment
  (def projector-raw
    (:body (http/get "https://ailev.livejournal.com/1758321.html")))
  (hickory-html projector-raw)
  (def html-article
    (first (jsoup/select (slurp "quq.html") "article.entry-content")))

  (hickory-html (:outer-html html-article))

  (bootleg/convert-to (:outer-html html-article) :hickory-seq)
  (spit "article.html" (:outer-html html-article))


  (spit "content.opf"
        (selmer/render-file "content.opf"
                            {:title "Попсовости обучения в нашем клубе не будет, давать \"короткие концерты по справочнику\" не согласны"
                             :uuid (java.util.UUID/randomUUID)
                             :now (.format java.time.format.DateTimeFormatter/ISO_INSTANT (java.time.Instant/now))}))

  (fs/zip "Попсовости обучения в нашем клубе не будет.epub"
          "Попсовости обучения в нашем клубе не будет, давать \"короткие концерты по справочнику\" не согласны"
          {:root "Попсовости обучения в нашем клубе не будет, давать \"короткие концерты по справочнику\" не согласны"})


  (spit "Section0001.xhtml"
        (selmer/render-file "Section0001.xhtml"
                            {:title "Попсовости обучения в нашем клубе не будет, давать \"короткие концерты по справочнику\" не согласны"
                             :article (:outer-html html-article)}))

  (first (jsoup/select (slurp "quq.html") "meta[property=og:title]"))

  (fs/copy-tree "epub-template" "Попсовости обучения в нашем клубе не будет, давать \"короткие концерты по справочнику\" не согласны")

  :rcf)

(comment
  (def projector-raw
    (:body (http/get "https://ailev.livejournal.com/1759764.html")))
  (hickory-html projector-raw)
  (def html-article
    (first (jsoup/select
            (slurp "https://ailev.livejournal.com/1759764.html")
            "article.entry-content")))

  (hickory-html (:outer-html html-article))

  (bootleg/convert-to (:outer-html html-article) :hickory-seq)
  (spit "article.html" (:outer-html html-article))
  :rcf)

(defn extract-article [sourse-html]
  (:outer-html (first (jsoup/select sourse-html "article.entry-content"))))


(defn -main [& args]
  (let [sourse-url (first (:args (cli/parse-args *command-line-args*)))
        sourse-html (slurp sourse-url)]
    (prn (subs (extract-article sourse-html) 0 80))
    (prn (:outer-html (first (jsoup/select sourse-html "meta[property=og:title]"))))))