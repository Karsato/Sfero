(ns sfero.reto
  (:require [sfero.core :as sfero]
            [sfero.registro :as registro]
            [clojure.java.io :as io])
  (:import [java.net ServerSocket Socket]
           [java.io DataOutputStream DataInputStream]))

(defn sendi-vektoron [host port vektoro]
  "Envía un vector a un nodo específico."
  (try
    (with-open [sock (Socket. host port)
                dos  (DataOutputStream. (.getOutputStream sock))]
      (.writeInt dos (count vektoro))
      (doseq [v vektoro] (.writeFloat dos (float v))))
    (catch Exception e (println "RETO > No se pudo conectar con" host ":" port))))

(defn sendi-transakcion [direccion vektoro nonce] 
  (let [[host port] (clojure.string/split direccion #":")]
    (try
      (with-open [sock (Socket. host (Integer/parseInt port))
                  dos  (DataOutputStream. (.getOutputStream sock))]
        ;; --- ESTA LÍNEA ES LA CLAVE ---
        (.writeByte dos 0) ;; 0 = Comando de Transacción
        ;; ------------------------------
        (.writeLong dos (long nonce))
        (.writeInt dos (count vektoro))
        (doseq [v vektoro] (.writeFloat dos (float v))))
      (catch Exception e nil))))

;; --- AJUSTE EN reto.clj ---
(defn lanzigi-servilon [port peers]
  (let [ss (ServerSocket. port)]
    (future
      (while true
        (try
          (with-open [sock (.accept ss)
                      dis  (DataInputStream. (.getInputStream sock))
                      dos  (DataOutputStream. (.getOutputStream sock))]
            (let [tipo-comando (.readByte dis)] ;; 0: TX, 1: SYNC_REQ
              (case tipo-comando
                0 (let [nonce (.readLong dis)
                        dim   (.readInt dis)
                        vektoro (vec (repeatedly dim #(.readFloat dis)))]
                    (when (and (registro/valida-minado? vektoro nonce)
                               (registro/ĉu-nova-transakcio? vektoro nonce))
                      (registro/aldoni-transakcion vektoro nonce)
                      (doseq [p peers] (sendi-transakcion p vektoro nonce))))
                
                1 (let [n-solicitado (.readInt dis)] ;; Nodo pide el archivo .n
                    (println "RETO > Petición de partición:" n-solicitado)
                    (let [nombre (str "sfero_cxeno." n-solicitado ".bin")
                          f (io/file nombre)]
                      (if (.exists f)
                        (do (.writeBoolean dos true)
                            (.writeUTF dos (slurp f)))
                        (.writeBoolean dos false)))
                    (.flush dos)))))
          (catch Exception e (println "RETO > Error:" (.getMessage e))))))))
