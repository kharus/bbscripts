(def word-list
  (with-open [reader (io/reader (io/file "Татарча.csv"))]
    (doall
     (csv/read-csv reader))))

(defn forward-line [line]
  (into ["Basic (type in the answer)"]
        (mapv str/lower-case line)))

(defn reverse-line [line]
  (into ["Basic (type in the answer)"]
        (mapv str/lower-case (reverse line))))

(comment
  (reverse-line ["Түгел" "не"]))

(defn forward-notes [words]
  (map forward-line words))

(defn reverse-notes [words]
  (map reverse-line words))

(comment
  (forward-notes (rest word-list)))

;; #separator:tab
;; #html:false
;; #notetype column:1

(with-open [writer (io/writer "Русча.anki.csv")]
  (.write writer "#separator:tab\n")
  (.write writer "#html:false\n")
  (.write writer "#notetype column:1\n")
  (csv/write-csv writer
                 (reverse-notes (rest word-list))
                 :separator (char \tab)))

