(require '[babashka.http-client :as http]
         '[babashka.json :as json]
         '[lambdaisland.uri :refer [uri]]
         '[babashka.fs :as fs]
         '[selmer.parser :as selmer]
         '[babashka.pods :as pods]
         '[meander.epsilon :as m])
(pods/load-pod 'retrogradeorbit/bootleg "0.1.9")

(require
 '[pod.retrogradeorbit.bootleg.utils :as bootleg]
 '[pod.retrogradeorbit.hickory.select :as s])

(def page-type-map
  {"HEADER" :header
   "TEXT" :text
   "TEST" :test})

(def headers-map-static
  {:headers
   {"accept" "application/json"
    "authority" "aisystant.system-school.ru"
    "referer" "https://aisystant.system-school.ru/lk/"
    "user-agent" "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/117.0.0.0 Safari/537.36"}})

(def headers-map
  (conj (:headers headers-map-static)
        ["dct" (slurp "dct.txt")]))

(def importing-data
  (json/read-str (slurp "course.json")))

(:course importing-data)

(def chapters
  (get-in importing-data [:course :chapters]))

(map #(select-keys % [:title :number]) chapters)

(def chapter-1
  (first chapters))

(first (:exercises chapter-1))

(def exercise-1 (first (:exercises chapter-1)))

(defn is-video? [exercise]
  (= "VideoExercise" (:type exercise)))

(def exercise-keys [:title :url])

(->> (:exercises chapter-1)
     (filter is-video?)
     (map #(select-keys % exercise-keys)))

(def video-page-raw
  (slurp "page.html"))

(second (re-find #"embedUrl\":\s+\"(.*?)\"" video-page-raw))

(defn hickory-html [raw-html]
  (->> (bootleg/convert-to raw-html :hickory-seq)
       (filter #(= (:tag %) :html))
       first))

(def projector-page (hickory-html (slurp "video-page.html")))

(s/select (s/id "videoData") projector-page)

(s/select (s/tag :table) projector-page)

(def video-page (hickory-html (slurp "page.html")))

(->> video-page
     (s/select (s/attr "type" #(= "application/ld+json" %)))
     first
     :content
     first
     json/read-str
     :embedUrl)

(def headers-qq
  {"accept" "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7"
   "user-agent" "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/129.0.0.0 Safari/537.36'"
   })

(def projector-raw 
  (:body (http/get "https://projector.datacamp.com/?projector_key=course_1329_0a9a8b15ef6a33602a2614a5484d63ac"
                   {:headers headers-qq})))

(s/select (s/id "videoData") (hickory-html projector-raw))

(->> (hickory-html projector-raw)
     (s/select (s/id "videoData"))
     first
     :attrs
     :value
     json/read-str
     :video_mp4_link)