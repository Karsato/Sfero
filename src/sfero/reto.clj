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

(defn sendi-transakcion [host port vektoro nonce]
  (try
    (with-open [sock (Socket. host port)
                dos  (DataOutputStream. (.getOutputStream sock))]
      (.writeLong dos (long nonce)) ;; 1. Enviamos el Nonce
      (.writeInt dos (count vektoro)) ;; 2. Enviamos la dimensión
      (doseq [v vektoro] (.writeFloat dos (float v))))
    (catch Exception e (println "RETO > Error de conexión con" port))))

(defn lanzigi-servilon [port peers]
  (let [ss (ServerSocket. port)]
    (future
      (while true
        (with-open [sock (.accept ss)
                    dis  (DataInputStream. (.getInputStream sock))]
          (let [nonce (.readLong dis) ;; 1. Leemos el Nonce
                dim   (.readInt dis)
                vektoro (vec (repeatedly dim #(.readFloat dis)))]
            
            ;; Usamos el nuevo sistema de verificación
            (if (registro/ĉu-nova-transakcio? vektoro nonce)
              (do
                (println (format "\nRETO > [Hash: %s] Nuevo. Propagando..." 
                                 (subs (sfero/haŝi-transakcion vektoro nonce) 0 8)))
                (registro/aldoni-transakcion vektoro)
                ;; Gossip: Reenviamos el nonce y el vector
                (doseq [p peers] (sendi-transakcion "localhost" p vektoro nonce)))
              (println "RETO > Transacción duplicada ignorada."))))))))
