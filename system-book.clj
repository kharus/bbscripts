(require '[babashka.http-client :as http]
         '[babashka.json :as json]
         '[lambdaisland.uri :refer [uri]]
         '[babashka.fs :as fs]
         '[clojure.edn :as edn]
         '[hiccup2.core :as h]
         '[selmer.parser :as selmer]
         '[babashka.pods :as pods])
(pods/load-pod 'retrogradeorbit/bootleg "0.1.9")


(require
 '[pod.retrogradeorbit.bootleg.utils :as bootleg]
 '[pod.retrogradeorbit.hickory.select :as s]
 '[pod.retrogradeorbit.hickory.render :as hick.render])


(def headers-map-static
  {:headers
   {"accept" "application/json"
    "authority" "aisystant.system-school.ru"
    "referer" "https://aisystant.system-school.ru/lk/"
    "user-agent" "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/117.0.0.0 Safari/537.36"}})

(def headers-map
  (conj (:headers headers-map-static)
        ["cookie" (slurp "cookie.txt")]))
(comment
  (def course-versions-web
    (http/get "https://aisystant.system-school.ru/api/courses/course-versions?course-path=intro-online-2021" {:headers headers-map}))
  :rcf)

(defn download-section [sec-num]
  (http/get (str "https://aisystant.system-school.ru/api/courses/text/" sec-num "?course-passing=9751") {:headers headers-map}))

(defn subpath [path-str]
  (let [path (fs/path path-str)
        name-count (.getNameCount path)]
    (.subpath path 1 name-count)))

(defn download-image [url]
  (io/copy
   (:body (http/get url {:as :stream :headers headers-map}))
   (fs/file (fs/path "." (subpath (:path (uri url)))))))

(def course-versions (json/read-str (slurp "intro-online-2021.json")))

(def course-sections (:sections (last course-versions)))

(def xf (comp
         (map #(select-keys % [:id :title]))
         (filter #(< 910 (:id %)))))

(comment
  (sequence xf course-sections)
  :rcf)

(def chapter4-sections
  [{:id 911, :title "Глава 4. Стек саморазвития"}
   {:id 912, :title "Вписанность в окружающий мир"}
   {:id 913, :title "Психопрактическое мастерство"}
   {:id 914, :title "Мастерство самолидерства"}
   {:id 915, :title "Мастерство эмоционального соответствия"}
   {:id 916, :title "Мастерство телесного соответствия"}
   {:id 1914, :title "Мастерство размышлений"}
   {:id 1915, :title "Развитие мастерства вписанности в мир"}
   {:id 917, :title "Выводы главы №4"}
   {:id 918, :title "Вопросы для повторения и дальнейшего изучения"}
   {:id 919, :title "Домашнее задание главы №4"}
   {:id 920, :title "Глава 5. Роль"}])

(comment
  (doseq [{section-id :id} chapter4-sections]
    (spit (str section-id ".html") (:body (download-section section-id))))
  :rcf)

(comment
  (doseq [{section-id :id section-title :title} chapter4-sections]
    (with-open [r (io/reader (str section-id ".html"))]
      (doseq [line (line-seq r)
              :let [img-matches (re-find #"img src=\"(.*?)\"" line)
                    img (second img-matches)]
              :when img-matches]
        (fs/create-dirs (fs/parent (subpath img)))
        (io/copy
         (:body (http/get (str "https://aisystant.system-school.ru" img) {:as :stream :headers headers-map}))
         (fs/file (fs/path "." (subpath img))))
        (prn img))))
  :rcf)

(comment
  (doseq [{section-id :id section-title :title} chapter4-sections]
    (spit (str section-id ".xhtml")
          (selmer/render-file "chapter.xhtml"
                              {:title section-title
                               :body (slurp (str section-id ".html"))})))
  :rcf)

(comment
  (doseq [{section-id :id section-title :title} chapter4-sections]
    (spit (str "wip/" section-id ".html")
          (selmer/render-file "section.html"
                              {:title section-title
                               :body (slurp (str section-id ".html"))})))
  :rcf)

(selmer/set-resource-path! "/home/ruslan/code/bbscripts/templates")

(comment
  "<div style=\"text-align: center;\">"
  "  <img alt=\"1\" src=\"../Images/1.png\"/>"
  "</div>")

(def html-wip (bootleg/convert-to (slurp "wip/913.xhtml") :hickory))

(spit "section-wip.html" (bootleg/as-html html-wip))

(require '[meander.epsilon :as m])
(def person
  {:name "jimmy"
   :preferred-address
   {:address1 "123 street ave"
    :address2 "apt 2"
    :city "Townville"
    :state "IN"
    :zip "46203"}})

(spit "section-wip.edn" html-wip)

(def h-image
  {:type :element,
   :attrs
   {:src "/text/intro-online-2021/2021-08-15/380/0.png",
    :alt ""},
   :tag :img,
   :content nil})

(:content html-wip)

(def dev-img {:type :element,
              :attrs {:style "text-align: center;"},
              :tag :div,
              :content
              [{:type :element,
                :attrs {:src "/text/intro-online-2021/2021-08-15/380/4.png", :alt ""},
                :tag :img, :content nil}]})



(def html-wip (bootleg/convert-to (str/replace (slurp "wip/913.xhtml") "\n" "") :hickory))

(def img-url "/text/intro-online-2021/2021-08-15/380/4.png")

(fs/parent img-url)

(fs/file-name (fs/parent img-url))

(defn flatten-image-path [image-path]
  (str
   "../Images/"
   (fs/file-name (fs/parent image-path)) "-" (fs/file-name image-path)))

(flatten-image-path img-url)

(bootleg/convert-to "<img src=\"/text/intro-online-2021/2021-08-15/380/4.png\" alt=\"\">" :hickory)

(comment 
  (with-open [r (io/reader "913.html")]
    (doseq [line (line-seq r)
            :let [img-matches (re-find #"img src=\"(.*?)\"" line)
                  img (second img-matches)]
            :when img-matches]
      (prn (bootleg/as-html
            (flatten-image-tag (bootleg/convert-to line :hickory))))))
  :rcf)

(defn flatten-image-tag [hickory-image]
  (m/match hickory-image
       {:type :element, 
        :attrs {:src ?image-url, :alt ""}, 
        :tag :img, 
        :content nil}

       {:type :element,
        :attrs {:src (flatten-image-path ?image-url)},
        :tag :img}))

(bootleg/as-html {:type :element, :attrs {:src "380-0.png"}, :tag :img})

(flatten-image-tag {:type :element, :attrs {:src "/text/intro-online-2021/2021-08-15/380/4.png", :alt ""}, :tag :img, :content nil})
