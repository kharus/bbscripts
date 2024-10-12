(require '[clojure.string :as str]
         '[babashka.fs :as fs])


(defn ordered-dirs [dir]
  (sort-by #(fs/file-name %)
           (filter fs/directory? (fs/list-dir dir))))

(defn week-number [week-path]
  (second (re-find #"(\d+)_" (fs/file-name week-path))))

(defn flatten-week [week-path]
  (let [week-id (week-number week-path)
        work-path  (fs/parent week-path)]
    (doseq [section-path (ordered-dirs week-path)]
      (fs/move section-path
               (fs/path work-path
                        (str week-id "w" (fs/file-name section-path)))))))

(defn flatten-course [course-dir]
  (let [course-weeks (ordered-dirs course-dir)]
    (run! flatten-week course-weeks)
    (run! fs/delete course-weeks)))

(comment
  (flatten-course "/home/ruslan/tmp/mooc/guitar-chords")
  :keep)