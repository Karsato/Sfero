(ns sfero.konservado
  (:require [clojure.java.io :as io])
  (:import [java.io DataOutputStream DataInputStream]))

(def arkivo-nomo "sfero_cxeno.bin")

(defn konservi-cxenon [data]
  (with-open [dos (DataOutputStream. (io/output-stream arkivo-nomo))]
    ;; Guardamos cuántos bloques hay
    (.writeInt dos (count (:arhivo data)))
    ;; Guardamos cada bloque
    (doseq [bloque (:arhivo data)]
      (.writeInt dos (:pezo bloque))
      (doseq [v (:v bloque)]
        (.writeFloat dos (float v))))
    ;; Guardamos el estado vivo (viva)
    (.writeInt dos (:kalkulilo data))
    (doseq [v (:viva data)]
      (.writeFloat dos (float v))))
  (println "SFERO > Registro binario sincronizado en disco."))

(defn sxargi-cxenon [dim]
  (let [f (io/file arkivo-nomo)]
    (if (.exists f)
      (try
        (with-open [dis (DataInputStream. (io/input-stream f))]
          (let [num-bloques (.readInt dis)
                arhivo (doall (repeatedly num-bloques 
                         (fn [] 
                           {:pezo (.readInt dis)
                            :v (vec (repeatedly dim #(.readFloat dis)))})))
                kalkulilo (.readInt dis)
                viva (vec (repeatedly dim #(.readFloat dis)))]
            {:arhivo arhivo :viva viva :kalkulilo kalkulilo}))
        (catch Exception e 
          (println "SFERO > Archivo corrupto o antiguo detectado. Iniciando nueva cadena.")
          nil)) ;; Si falla, devolvemos nil para que registro cree una limpia
      nil)))
