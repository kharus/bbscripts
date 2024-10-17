(with-open [reader (io/reader (io/file "Татарча.csv"))]
  (doall
   (csv/read-csv reader)))