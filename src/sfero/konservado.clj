(ns sfero.konservado
  (:require [clojure.java.io :as io])
  (:import [java.io DataOutputStream DataInputStream]))

(def arkivo-nomo "sfero_cxeno.bin")

(defn konservi-cxenon
  ([data] (konservi-cxenon data arkivo-nomo))
  ([data nomo]
   (with-open [dos (DataOutputStream. (io/output-stream nomo))]
     ;; 1. Guardamos los bloques del archivo (arhivo)
     (.writeInt dos (count (:arhivo data)))
     (doseq [bloque (:arhivo data)]
       (.writeInt dos (:pezo bloque))
       (doseq [v (:v bloque)]
         (.writeFloat dos (float v))))
     
     ;; 2. Guardamos el estado vivo (viva)
     (.writeInt dos (:kalkulilo data))
     (doseq [v (:viva data)]
       (.writeFloat dos (float v)))
     
     ;; 3. NUEVO: Guardamos los nonces (hashes de transacciones vistas)
     (let [nonces (:nonces data)]
       (.writeInt dos (count nonces))
       (doseq [h nonces]
         (.writeUTF dos h)))) ;; writeUTF es ideal para strings de hashes
   (println (str "SFERO > Datos sincronizados en: " nomo))))

(defn sxargi-cxenon-el-dosiero [f-nomo dim]
  (let [f (io/file f-nomo)]
    (if (.exists f)
      (try
        (with-open [dis (DataInputStream. (io/input-stream f))]
          (let [num-bloques (.readInt dis)
                arhivo (doall (repeatedly num-bloques  
                         (fn []  
                           {:pezo (.readInt dis)
                            :v (vec (repeatedly dim #(.readFloat dis)))})))
                kalkulilo (.readInt dis)
                viva (vec (repeatedly dim #(.readFloat dis)))
                ;; Leemos los nonces
                num-nonces (try (.readInt dis) (catch Exception _ 0))
                nonces (set (repeatedly num-nonces #(.readUTF dis)))]
            {:arhivo arhivo :viva viva :kalkulilo kalkulilo :nonces nonces}))
        (catch Exception e  
          (println "SFERO > Error leyendo " f-nomo ": " (.getMessage e))
          nil))
      nil)))

(defn sxargi-cxenon [dim]
  (sxargi-cxenon-el-dosiero arkivo-nomo dim))
