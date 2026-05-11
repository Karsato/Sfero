(ns sfero.reto
  (:require [sfero.core :as sfero]
            [sfero.registro :as registro]
            [clojure.java.io :as io])
  (:import [java.net ServerSocket Socket]
           [java.io DataOutputStream DataInputStream]))

(defn sendi-transakcion [direccion vektoro nonce] 
  (let [[host port] (clojure.string/split direccion #":")]
    (try
      (with-open [sock (Socket. host (Integer/parseInt port))
                  dos  (DataOutputStream. (.getOutputStream sock))]
        (.writeByte dos 0) ;; Comando 0: Transacción
        (.writeLong dos (long nonce))
        (.writeInt dos (count vektoro))
        (doseq [v vektoro] (.writeFloat dos (float v))))
      (catch Exception e nil))))

(defn lanzigi-servilon [port peers]
  (let [ss (ServerSocket. port)]
    (future
      (while true
        (try
          (with-open [sock (.accept ss)
                      dis  (DataInputStream. (.getInputStream sock))
                      dos  (DataOutputStream. (.getOutputStream sock))]
            (let [tipo (.readByte dis)]
              (case tipo
                0 (let [nonce (.readLong dis)
                        dim (.readInt dis)
                        vektoro (vec (repeatedly dim #(.readFloat dis)))]
                    (when (and (registro/valida-minado? vektoro nonce)
                               (registro/ĉu-nova-transakcio? vektoro nonce))
                      (registro/aldoni-transakcion vektoro nonce)
                      (doseq [p peers] (sendi-transakcion p vektoro nonce))))
                
                1 (let [n (.readInt dis)
                        f (io/file (str "sfero_cxeno." n ".bin"))]
                    (if (.exists f)
                      (do (.writeBoolean dos true)
                          (.writeLong dos (.length f))
                          (io/copy f dos))
                      (.writeBoolean dos false)))
                (println "RETO > Comando desconocido"))))
          (catch Exception e (println "RETO > Error:" (.getMessage e))))))))
