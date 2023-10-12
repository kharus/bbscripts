(require '[babashka.http-client :as http])
(require '[babashka.json :as json])
(require '[babashka.fs :as fs])
(require '[clojure.edn :as edn])

(def headers-map 
  {:headers 
   {"accept" "application/json"
    "authority" "aisystant.system-school.ru"
   	"referer" "https://aisystant.system-school.ru/lk/"
    "user-agent" "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/117.0.0.0 Safari/537.36"
   	}})

(conj (:headers headers-map)
      ["cookie"
       ""])

(http/get "https://aisystant.system-school.ru/api/courses/text/15028?course-passing=17591" headers-map)

(defn download-section [sec-num]
  (http/get (str "https://aisystant.system-school.ru/api/courses/text/" sec-num "?course-passing=17591") headers-map))

(json/parse-string "true")

(def course-versions (json/read-str (slurp "course-versions.json")))


(def course-sections (:sections (last course-versions)))

(def xf (comp 
         (map #(select-keys % [:id :title]))
         (filter #(< 15043 (:id %))
         )))

(sequence xf course-sections)

(doseq [{section-id "id"} chapter3-sections]
  (spit (str section-id ".html") (:body (download-section section-id))))

(doseq [{section-id "id" section-title "title"} chapter3-sections]
  (spit "jumbo.html" 
        (str "<section>\n"
           		"<h2>" section-title "</h2>\n"
         (:body (edn/read-string (slurp (str section-id ".html"))))
          		 "</section>\n")
      		:append true))


(:body (edn/read-string (slurp (str 15029 ".html"))))

