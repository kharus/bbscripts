(require '[babashka.http-client :as http])
(require '[babashka.json :as json])
(require '[babashka.fs :as fs])
(require '[clojure.edn :as edn])

(def headers-map-static
  {:headers 
   {"accept" "application/json"
    "authority" "aisystant.system-school.ru"
   	"referer" "https://aisystant.system-school.ru/lk/"
    "user-agent" "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/117.0.0.0 Safari/537.36"}})

(def headers-map
  (conj (:headers headers-map-static)
        ["cookie" (slurp "cookie.txt")]))

(http/get "https://aisystant.system-school.ru/api/courses/text/15028?course-passing=17591" {:headers headers-map})

(defn download-section [sec-num]
  (http/get (str "https://aisystant.system-school.ru/api/courses/text/" sec-num "?course-passing=17591") {:headers headers-map}))

(def course-versions (json/read-str (slurp "course-versions.json")))

(def course-sections (:sections (last course-versions)))

(def xf (comp 
         (map #(select-keys % [:id :title]))
         (filter #(< 15128 (:id %))
         )))

(sequence xf course-sections)

(def chapter4-sections
  [{:id 15129, :title "9. Практика планирования"}
   {:id 15130, :title "Неудовлетворенности, за которые отвечает планирование"}
   {:id 15131, :title "Почему возникают проблемы с планированием?"}
   {:id 15132, :title "Что такое планирование?"}
   {:id 15133, :title "Что мы можем получить от правильного планирования?"}
   {:id 15134, :title "Для кого важно планирование"}
   {:id 15135, :title "Мастерство планирования"}
   {:id 15136, :title "Что делает человек, занимающийся личным планированием?"}
   {:id 15137, :title "Ресурсы для планирования"}
   {:id 15138, :title "Рабочие продукты по практике планирования"}
   {:id 15139, :title "Принципы практики планирования"}
   {:id 15140, :title "Как практикам планирования связана с другими практиками саморазвития"}
   {:id 15141, :title "Как освоить практику планирования. Первый этап — уровень “Объяснения”"}
   {:id 15142, :title "Как освоить практику планирования Второй этап – «Умение»"}
   {:id 15143, :title "Как освоить практику планирования. Третий и четвертый этапы – «Навык» и «Мастерство»"}
   {:id 15144, :title "Моделирование: ответьте на вопросы по изученному материалу"}
   {:id 15145, :title "Дополнительные материалы"}
   {:id 15146, :title "Домашнее задание 9"}
   {:id 15147, :title "Что дальше"}
]
  )

(doseq [{section-id :id} chapter4-sections]
  (spit (str section-id ".html") (:body (download-section section-id))))

(download-section 15050)

(doseq [{section-id :id section-title :title} chapter4-sections]
  (spit "jumbo6.html" 
        (str "<section>\n"
           		"<h2>" section-title "</h2>\n"
         (slurp (str section-id ".html"))
          		 "</section>\n")
      		:append true))


(:body (edn/read-string (slurp (str 15029 ".html"))))

