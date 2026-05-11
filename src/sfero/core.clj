(ns sfero.core
  (:import [java.util Random]))

;; Dimensionalidad maestra de la red Sfero
(def ^:const dim 10000)

;; --- GENERACIÓN DE IDENTIDAD ---

(defn naski-vektoron [semilla]
  "Genera un vector base (semilla) para identidades o activos."
  (let [rng (Random. semilla)]
    (vec (repeatedly dim #(if (.nextBoolean rng) 1.0 -1.0)))))

;; --- OPERACIONES DEL PROTOCOLO (Álgebra VSA) ---

(defn permutado [v n]
  "Permutación (Rotación): Da orden cronológico o posicional."
  (let [c (count v)
        shift (mod n c)]
    (if (zero? shift)
      v
      (let [split-idx (- c shift)]
        (vec (concat (subvec v split-idx) (subvec v 0 split-idx)))))))

(defn kunigo [vektoroj]
  "Superposición (Bundling): Suma transacciones en la 'hiperesfera'."
  (reduce (fn [acc v] (mapv + acc v)) 
          (vec (repeat dim 0.0)) 
          vektoroj))

(defn ligi [v1 v2]
  "Vínculo (Binding): Une una identidad con su valor o derecho."
  (mapv * v1 v2))

;; --- CONGREUENCIA Y RESONANCIA ---

(defn resonanco [v1 v2]
  "Calcula la similitud geométrica (Producto Punto Normalizado)."
  (let [v1-arr (double-array v1)
        v2-arr (double-array v2)]
    (loop [i 0 acc 0.0]
      (if (< i dim)
        (recur (inc i) (+ acc (* (aget v1-arr i) (aget v2-arr i))))
        (/ acc (double dim))))))

(defn serĉi-plej-proksiman [target-vector memoro]
  "Busca el rastro más cercano en el historial de la red."
  (apply max-key (fn [[_ v]] (resonanco target-vector v)) memoro))

(defn normalizi [v]
  "Normaliza el vector para que sus valores vuelvan a estar cerca de 1 o -1, 
   evitando que la 'plastilina' se desborde."
  (mapv (fn [x] (cond (> x 0) 1.0 (< x 0) -1.0 :else 0.0)) v))
