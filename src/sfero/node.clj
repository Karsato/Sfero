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
    (reto/lanzigi-servilon mi-puerto peers)

    (let [alice (monujo/krei-monujon "alice")
          monero (sfero/naski-vektoron 999)
          id-alice (sfero/ligi monero (:id-vektoro alice))]

      ;; Bucle interactivo simple
      (loop []
        (println "\nComandos: [1] Ver Saldo | [2] Enviar 1.0 SFE a la red | [0] Salir")
        (let [opcion (read-line)]
          (case opcion
            "1" (println "SALDO ACTUAL:" (registro/kalkuli-ekvilibron id-alice) "SFE")
            "2" (let [monto 1.0
          nonce (rand-nth (range 1000000000)) ;; Generamos un Nonce aleatorio
          pago (sfero/skali id-alice monto)]
      (if (registro/provi-transakcion id-alice monto 
            (fn [] 
              ;; Registramos localmente con el nonce
              (registro/ĉu-nova-transakcio? pago nonce) 
              (registro/aldoni-transakcion pago)
              ;; Enviamos a la red
              (doseq [p peers] (reto/sendi-transakcion "localhost" p pago nonce))))
        (println ">>> DIFUNDIDO: Transacción única enviada con éxito.")
        (println ">>> RECHAZADO: Fondos insuficientes.")))
            "0" (System/exit 0)
            nil)
          (recur))))))
