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

(defn lanzigi-servilon [port peers]
  (let [ss (ServerSocket. port)]
    (future
      (while true
        (with-open [sock (.accept ss)
                    dis  (DataInputStream. (.getInputStream sock))]
          (let [dim (.readInt dis)
                vektoro (vec (repeatedly dim #(.readFloat dis)))]
            ;; --- FILTRO DE DUPLICADOS ---
            (if (registro/ĉu-nova? vektoro)
              (do
                (println "\nRETO > Nuevo vector detectado. Integrando y propagando...")
                (registro/aldoni-transakcion vektoro)
                ;; Propagar a los demás (Gossip)
                (doseq [p peers]
                  (sendi-vektoron "localhost" p vektoro)))
              (println "RETO > Vector duplicado ignorado (evitando bucle)."))))))))
