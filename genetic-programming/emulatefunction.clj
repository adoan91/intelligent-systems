(ns fungp.emulatefunction
  (:use fungp.core)
  (:use fungp.util)
  (:use clojure.pprint))

(def criteria 0.01)

;  (x * x) - (y * y) + 500sin(x)
(defn our-function
    [x y] (+ (- (* x x) (* y y)) (* 500 (sin x)))
    )

(def sample-functions
  '[[+ 2]
    [- 2]
    [* 2]
    [fungp.util/abs 1]
    ; [fungp.util/sdiv 2]
    [fungp.util/sin 1]
    [inc 1]
    [dec 1]])

(def sample-parameters
    ['x 'y])

(def number-literals
  '[1 2 3 4 5 6 7 8 9 10 500])

; 50 sampled points
(def in-list1 '(-8 -7 -6.283185307 -6 -5 -4.71238898 -4 -3.141592654 -3 -2 -1.570796327 -1 0 1 1.570796327 2 3 3.141592654 4 4.71238898 5 6 6.283185307 7 8 9 10 100 200 300 400 500 600 56 87 50 95 43 94 63 3 8))
(def in-list2 '(22 60 65 45 82 25 58 32 81 16 51 68 85 4 71 56 91 35 45 24 70 0 44 11 16 30 83 58 15 14 21 91 17 72 2 62 24 42 48 63 94 44 18 1 73 49 56 15 15 0))
(def out-list (map our-function in-list1 in-list2))

(defn sample-fitness
  [tree]
  (try
    (let [f (compile-tree tree sample-parameters)
          results (map f in-list1 in-list2)]
      (let [r (reduce + (map off-by-sq out-list results))]
         (if (< r criteria) 0 r)))
    (catch Exception e (println e) (println tree))))

(defn sample-report
  [tree fitness]
  (pprint tree)
  (println (str "Error:\t" fitness "\n"))
  (flush))

(defn test-regression2
  [n1 n2]
  (println "\nfungp :: Functional Genetic Programming in Clojure")
  (println "Mike Vollmer, 2012")
  (println (str "Test inputs: " (vec in-list1)))
  (println (str "Test inputs: " (vec in-list2)))
  (println (str "Test outputs: " (vec out-list)))
  (println (str "Max generations: " (* n1 n2)))
  (println)
  (let [options {:iterations n1
                 :migrations n2
                 :num-islands 30;6
                 :population-size 100;40
                 :tournament-size 10;5
                 :mutation-probability 0.3;0.1
                 :max-depth 10
                 :terminals sample-parameters
                 :numbers number-literals
                 :fitness sample-fitness
                 :functions sample-functions
                 :report sample-report }
        [tree score] (rest (run-genetic-programming options))]
    (do (println "Done!")
        (sample-report tree score))))
