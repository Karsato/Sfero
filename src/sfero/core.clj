(ns sfero.core
  (:import [java.util Random])
  (:import [java.security MessageDigest]))

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

(defn binarigi [v]
  "Convierte el vector resultante en un vector bipolar puro (1.0 o -1.0).
   Esto es vital para mantener la capacidad de la memoria de la red."
  (mapv #(if (>= % 0) 1.0 -1.0) v))

(defn malkresko [v faktoro]
  "Reduce la intensidad de todos los elementos del vector.
   El faktoro suele ser algo como 0.95 (pierde un 5% de fuerza)."
  (mapv #(* % (double faktoro)) v))

(defn inversi [v]
  "Invierte la polaridad del vector para representar una resta en el hiperespacio."
  (mapv #(* % -1.0) v))

(defn skali [v faktoro]
  "Cambia la intensidad del vector. Útil para aplicar comisiones o pesos."
  (mapv #(* % (double faktoro)) v))

(defn skani-registron [registro vortaro]
  "Escanea el registro comparándolo con un diccionario de vectores conocidos.
   Devuelve un ranking de lo que más resuena."
  (->> vortaro
       (map (fn [[nomo v]] [nomo (resonanco registro v)]))
       (sort-by second >)))

(defn haŝi-transakcion [vektoro nonce] ;; <-- Nombre actualizado
  (let [md (java.security.MessageDigest/getInstance "SHA-256")
        bb (java.nio.ByteBuffer/allocate (+ (* 4 (count vektoro)) 8))]
    (doseq [v vektoro] (.putFloat bb (float v)))
    (.putLong bb (long nonce))
    (let [bytes (.digest md (.array bb))]
      (apply str (map #(format "%02x" %) bytes)))))
