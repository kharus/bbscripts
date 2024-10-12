(require '[babashka.fs :as fs])
(require '[clojure.string :as str])

(def lines (fs/read-all-lines "defi.py.timepoints"))
(def end "16:22:11")

(defn first-and-last [s]
  ((juxt first last)
   (str/split s #",\s*")))

(defn clear-dot [s]
  (if (str/ends-with? s ".")
    (clojure.string/replace s #"\.$" "")
    s))

(def times
  (->> lines
       (map #(str/split % #" " 2))
       (map first)))

(def start-end
  (partition 2 1 (concat times (list end))))

(def names (->> lines
                (map #(str/split % #" " 2))
                (map last)
                (map list)))

(def tracks (map concat start-end names))

(def n-tracks (map conj tracks (range)))

(defn render-command [parts]
  (let [[f-number f-start f-end name-first] parts
        track-number (format "%02d" f-number)]
    (str "ffmpeg -ss " f-start " -to " f-end " -i Web3.py.mp4 -c copy \"" track-number ". " name-first ".mp4\"")))

(run! println
      (map render-command n-tracks))