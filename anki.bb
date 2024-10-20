(defn anki-line [line]
  (into ["Basic (type in the answer)"]
        (mapv str/lower-case line)))

(defn forward-notes [words-line]
  (map anki-line words-line))

(defn reverse-notes [words-line]
  (map (comp anki-line reverse) words-line))

(defn read-lines [word-filename]
  (with-open [reader (io/reader (io/file word-filename))]
    (doall (csv/read-csv reader))))

(defn save-anki [anki-filename anki-lines]
  (with-open [writer (io/writer anki-filename)]
    (.write writer "#separator:tab\n")
    (.write writer "#html:false\n")
    (.write writer "#notetype column:1\n")
    (csv/write-csv writer
                   anki-lines
                   :separator (char \tab))))



(comment
  (let [dict-lines (rest (read-lines "Татарча.csv"))]
    (save-anki "litvinov.tat.txt" (forward-notes dict-lines))
    (save-anki "litvinov.rus.txt" (reverse-notes dict-lines))))