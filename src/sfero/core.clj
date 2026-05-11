(ns sfero.core
  (:import [java.util Random]))

(def ^:const dim 10000)

(defn naski-vektoron [semilla]
  (let [rng (Random. semilla)]
    (vec (repeatedly dim #(if (.nextBoolean rng) 1.0 -1.0)))))

(defn permutado [v n]
  (let [c (count v)
        shift (mod n c)]
    (if (zero? shift)
      v
      (let [split-idx (- c shift)]
        (vec (concat (subvec v split-idx) (subvec v 0 split-idx)))))))

(defn kunigo [vektoroj]
  "Superposición (Bundling) - La suma de la plastilina."
  (reduce (fn [acc v] (mapv + acc v)) 
          (vec (repeat dim 0.0)) 
          vektoroj))

(defn ligi [v1 v2]
  "Vínculo (Binding) - El producto de Hadamard."
  (mapv * v1 v2))

(defn normalizi [v]
  "Mantiene los valores del vector en {-1, 1}."
  (mapv #(cond (> % 0) 1.0 (< % 0) -1.0 :else 0.0) v))

(defn resonanco [v1 v2]
  (let [v1-arr (double-array v1)
        v2-arr (double-array v2)]
    (loop [i 0 acc 0.0]
      (if (< i dim)
        (recur (inc i) (+ acc (* (aget v1-arr i) (aget v2-arr i))))
        (/ acc (double dim))))))

(defn kodigi-tekston [teksto alfabeto]
  "Transforma texto en un hipervector usando el alfabeto de la red."
  (let [tokens (clojure.string/lower-case teksto)
        vektoroj (map-indexed (fn [idx c] 
                                (permutado (get alfabeto c) idx)) 
                              tokens)]
    (kunigo vektoroj)))
