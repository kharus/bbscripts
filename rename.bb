(require '[babashka.fs :as fs])

(def short-names
  (->> (fs/glob "/home/ruslan/tmp" "Ep. * - Marketing Mix Modeling*")
       (map fs/file-name)
       (map #(str/replace % "Marketing Mix Modeling：" ""))
       (map #(str/replace % #" (\d) " " 0$1 "))))


(defn rename-lex [wip-name]
  (str/replace wip-name #"(.*) ｜ Lex Fridman Podcast #(\d+)\.(.*)" "$2-$1.$3"))

(defn rename-ai4ce [wip-name]
  (str/replace wip-name #"(.*) \[.* (\d+)\]\.(.*)" "$2-$1.$3"))

(defn rename-barry [wip-name]
  (str/replace wip-name #"Analytic Metaphysics (\d+)： (.*)" (fn [[_ a b]] (str (format "%02d" (Integer/parseInt a)) "-" b))))


(defn rename-sean [wip-name]
  (str/replace wip-name #"The Biggest Ideas in the Universe ｜ Q&A (\d+) - (.*)"
               (fn [[_ a b]] (str (format "%02d" (Integer/parseInt a)) ".QA-" b))))


(defn rename-uuid [wip-name]
  (str/replace wip-name #"(.*)-[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}-mp4_720p(.*)" "$1$2"))


(defn rename-tat [wip-name]
  (str/replace wip-name #".*#(\d+)\.(.*)"
               (fn [[_ a b]] (str 
                              "Татарча өйрәнәбез дәрес #"
                              (format "%03d" (Integer/parseInt a))
                              "." b))))


(comment
  (rename-tat "Татарча ойрэнэбез ｜ Учим татарский ｜ Дэрес #1.webm"))


(defn copy-file [wip-file]
  (let [wip-name (fs/file-name wip-file)
        new-name (rename-tat wip-name)
        new-path (str (fs/parent wip-file) "/" new-name)]
    (fs/move wip-file new-path)))
    ;(println new-name)))

(comment
  (->>
   (fs/glob "/home/ruslan/tmp/Татарча ойрэнэбез" "*.webm")
   (run! copy-file)))


(def wip-file
  (fs/file "/home/ruslan/tmp/Policy_for_Science_Technology_Innovation/09-Class_8_The_Emergence_of_Industrial_Innovation_Policy/03-Wrapping_Up/08-Wrapping_Up-db378552-699b-433c-aa4f-41add96c5ca4-mp4_720p.mp4"))


(let [wip-name (fs/file-name wip-file)
      new-name (rename-uuid wip-name)
      new-path (str (fs/parent wip-file) "/" new-name)]
  (fs/move wip-file new-path))

