(ns sfero.node
  (:require [sfero.core :as sfero]
            [sfero.monujo :as monujo]
            [sfero.registro :as registro]
            [sfero.reto :as reto]))

(defn -main [& args]
  ;; args: [mi-puerto peer1-puerto peer2-puerto ...]
  (let [mi-puerto (Integer/parseInt (or (first args) "8080"))
        peers (map #(Integer/parseInt %) (rest args))]
    
    (println "====================================")
    (println "      SFERO NETWORK - Nodo Activo   ")
    (println "      Puerto:" mi-puerto " Peers:" (vec peers))
    (println "====================================")

    ;; Iniciamos el servidor para recibir transacciones
    (reto/lanzigi-servilon mi-puerto)

    (let [alice (monujo/krei-monujon "alice")
          monero (sfero/naski-vektoron 999)
          id-alice (sfero/ligi monero (:id-vektoro alice))]

      ;; Bucle interactivo simple
      (loop []
        (println "\nComandos: [1] Ver Saldo | [2] Enviar 1.0 SFE a la red | [0] Salir")
        (let [opcion (read-line)]
          (case opcion
            "1" (println "SALDO ACTUAL:" (registro/kalkuli-ekvilibron id-alice) "SFE")
            "2" (let [pago (sfero/skali id-alice 1.0)]
                  ;; Si el guardián lo aprueba, lo enviamos a los demás
                  (if (registro/provi-transakcion id-alice 1.0 
                        (fn [] 
                          (registro/aldoni-transakcion pago)
                          (doseq [p peers] (reto/sendi-vektoron "localhost" p pago))))
                    (println ">>> DIFUNDIDO: El vector está viajando por la red.")
                    (println ">>> RECHAZADO: No tienes saldo para difundir.")))
            "0" (System/exit 0)
            nil)
          (recur))))))
