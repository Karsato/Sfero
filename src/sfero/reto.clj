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

(defn sendi-transakcion [direccion vektoro nonce] ;; 'direccion' es "node2:8080"
  (let [[host port] (clojure.string/split direccion #":")]
    (try
      (with-open [sock (Socket. host (Integer/parseInt port))
                  dos  (DataOutputStream. (.getOutputStream sock))]
        (.writeLong dos (long nonce))
        (.writeInt dos (count vektoro))
        (doseq [v vektoro] (.writeFloat dos (float v))))
      (catch Exception e 
        ;; Silenciamos el error si el peer aún no está levantado
        nil))))

(defn lanzigi-servilon [port peers]
  (let [ss (ServerSocket. port)]
    (future
      (while true
        (try
          (with-open [sock (.accept ss)
                      dis  (DataInputStream. (.getInputStream sock))]
            (let [nonce (.readLong dis)
                  dim   (.readInt dis)
                  vektoro (vec (repeatedly dim #(.readFloat dis)))]
              (if (and (registro/valida-minado? vektoro nonce) ;; <-- VALIDA EL TRABAJO
                       (registro/ĉu-nova-transakcio? vektoro nonce))
                (do
                  (println (format "\nRETO > [%s] Recibido. Propagando..." 
                                   (subs (sfero/haŝi-transakcion vektoro nonce) 0 8)))
                  (registro/aldoni-transakcion vektoro)
                  ;; Propagamos a la lista de direcciones host:port
                  (doseq [p peers] (sendi-transakcion p vektoro nonce)))
                nil)))
          (catch Exception e (println "RETO > Error en recepción:" (.getMessage e))))))))
