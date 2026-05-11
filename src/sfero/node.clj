(ns sfero.node
  (:require [sfero.core :as sfero]
            [sfero.monujo :as monujo]
            [sfero.registro :as registro]
            [sfero.reto :as reto]))

(defn -main [& args]
  (let [mi-puerto (Integer/parseInt (or (first args) "8080"))
        peers (map #(Integer/parseInt %) (rest args))]
    
    (println "====================================")
    (println "      SFERO NETWORK - Nodo Activo   ")
    (println "      Puerto:" mi-puerto " Peers:" (vec peers))
    (println "====================================")

    ;; Iniciamos el servidor de red
    (reto/lanzigi-servilon mi-puerto peers)

    (let [alice  (monujo/krei-monujon "alice")
          bob    (monujo/krei-monujon "bob")
          monero (sfero/naski-vektoron 999)
          
          ;; Identidades Geométricas puras
          id-alice (:id-vektoro alice)
          id-bob   (:id-vektoro bob)
          
          ;; Identidades vinculadas a la moneda para saldos
          v-alice (sfero/ligi monero id-alice)
          v-bob   (sfero/ligi monero id-bob)
          
          ;; El Cofre (Multisig) basado en identidades
          id-cofre (sfero/krei-komunidenton [id-alice id-bob])]

      (loop []
        (println "\n--- MENÚ DE OPERACIONES ---")
        (println "[1] Ver Saldos (Alice/Bob/Cofre)")
        (println "[2] Enviar 1.0 SFE (Alice -> Red)")
        (println "[3] Test Cerradura Multisig")
        (println "[4] RETIRO REAL: 5.0 SFE del Cofre -> Bob")
        (println "[0] Salir")
        
        (let [opcion (read-line)]
          (case opcion
            "1" (do
                  (println (format "SALDO ALICE: %.4f SFE" (registro/kalkuli-ekvilibron v-alice)))
                  (println (format "SALDO BOB:   %.4f SFE" (registro/kalkuli-ekvilibron v-bob)))
                  (println (format "SALDO COFRE: %.4f SFE" (registro/kalkuli-ekvilibron id-cofre))))
            
            "2" (let [monto 1.0
                      nonce (rand-nth (range 1000000000))
                      pago (sfero/skali v-alice monto)]
                  (if (registro/provi-transakcion v-alice monto 
                        (fn [] 
                          (registro/ĉu-nova-transakcio? pago nonce) 
                          (registro/aldoni-transakcion pago)
                          (doseq [p peers] (reto/sendi-transakcion "localhost" p pago nonce))))
                    (println ">>> DIFUNDIDO: 1.0 SFE de Alice viaja por la red.")
                    (println ">>> RECHAZADO: Alice no tiene fondos suficientes.")))

            "3" (do
                  (println "\n--- TEST DE CERRADURA ---")
                  (println "Probando Alice sola...")
                  (registro/provi-multisig id-cofre id-alice 1.0 (fn [] nil))
                  (println "\nProbando Alice + Bob...")
                  (let [firma (sfero/kunigo [id-alice id-bob])]
                    (registro/provi-multisig id-cofre firma 1.0 (fn [] nil))))

            "4" (let [monto 5.0
                      nonce (rand-nth (range 1000000000))
                      v-debito  (sfero/skali (sfero/inversi id-cofre) monto)
                      v-kredito (sfero/skali v-bob monto)
                      transfero (sfero/kunigo [v-debito v-kredito])
                      firma-conjunta (sfero/kunigo [id-alice id-bob])]
                  
                  (println "\n--- SOLICITUD DE RETIRO MULTISIG ---")
                  (if (registro/provi-multisig id-cofre firma-conjunta monto
                        (fn []
                          (registro/ĉu-nova-transakcio? transfero nonce)
                          (registro/aldoni-transakcion transfero)
                          (doseq [p peers] (reto/sendi-transakcion "localhost" p transfero nonce))))
                    (println (format ">>> ÉXITO: %.1f SFE movidos del Cofre a Bob." monto))
                    (println ">>> ERROR: Firma inválida o fondos insuficientes en el cofre.")))

            "0" (System/exit 0)
            (println "Opción no reconocida."))
          (recur))))))
