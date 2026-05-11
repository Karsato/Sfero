(ns sfero.node
  (:require [sfero.core :as sfero]
            [sfero.monujo :as monujo]
            [sfero.registro :as registro]
            [sfero.reto :as reto]))

(defn -main [& args]
  (let [mi-puerto (Integer/parseInt (or (first args) "8080"))
        peers (rest args)]
    
    (println "====================================")
    (println "      SFERO NETWORK - Nodo Activo   ")
    (println "      Puerto:" mi-puerto " Peers:" (vec peers))
    (println "====================================")

    (reto/lanzigi-servilon mi-puerto peers)

    (let [alice  (monujo/krei-monujon "alice")
          bob    (monujo/krei-monujon "bob")
          monero (sfero/naski-vektoron 999)
          
          id-alice (:id-vektoro alice)
          id-bob   (:id-vektoro bob)
          
          v-alice (sfero/ligi monero id-alice)
          v-bob   (sfero/ligi monero id-bob)
          
          id-cofre (sfero/krei-komunidenton [id-alice id-bob])]

      (loop []
        (println "\n--- MENÚ DE OPERACIONES ---")
        (println "[1] Ver Saldos (Alice/Bob/Cofre)")
        (println "[2] Alice +1.0 SFE (Mina 1.0)")
        (println "[3] Test Cerradura Multisig")
        (println "[4] RETIRO COFRE: 5.0 -> Bob (Multisig + Mina)")
        (println "[5] TRANSFERIR: 2.0 (Alice -> Bob + Mina)")
        (println "[0] Salir")
        
        (let [opcion (read-line)]
          (if (nil? opcion)
            (do (Thread/sleep 1000) (recur))
            (do
              (case opcion
                "1" (do
                      (println (format "SALDO ALICE: %.4f SFE" (registro/kalkuli-ekvilibron v-alice)))
                      (println (format "SALDO BOB:   %.4f SFE" (registro/kalkuli-ekvilibron v-bob)))
                      (println (format "SALDO COFRE: %.4f SFE" (registro/kalkuli-ekvilibron id-cofre))))
                
                "2" (let [monto 1.0
                          pago (sfero/skali v-alice monto)]
                      (if (registro/provi-transakcion v-alice monto   
                            (fn []  
                              (let [nonce (registro/mini-vektoron pago)]
                                (registro/ĉu-nova-transakcio? pago nonce)  
                                (registro/aldoni-transakcion pago)
                                (doseq [p peers] (reto/sendi-transakcion p pago nonce)))))
                        (println ">>> ÉXITO: 1.0 SFE minado y difundido.")
                        (println ">>> ERROR: Fondos insuficientes.")))

                "3" (do
                      (println "\n--- TEST DE CERRADURA ---")
                      (let [firma (sfero/kunigo [id-alice id-bob])]
                        (registro/provi-multisig id-cofre firma 1.0 (fn [] (println "Cerradura OK")))))

                "4" (let [monto 5.0
                          v-debito  (sfero/skali (sfero/inversi id-cofre) monto)
                          v-kredito (sfero/skali v-bob monto)
                          transfero (sfero/kunigo [v-debito v-kredito])
                          firma-conjunta (sfero/kunigo [id-alice id-bob])]
                      (if (registro/provi-multisig id-cofre firma-conjunta monto
                            (fn []
                              (let [nonce (registro/mini-vektoron transfero)]
                                (registro/ĉu-nova-transakcio? transfero nonce)
                                (registro/aldoni-transakcion transfero)
                                (doseq [p peers] (reto/sendi-transakcion p transfero nonce)))))
                        (println ">>> ÉXITO: Retiro minado y enviado.")
                        (println ">>> ERROR: Firma o fondos inválidos.")))

                "5" (let [monto 2.0
                          v-debito  (sfero/skali (sfero/inversi v-alice) monto)
                          v-kredito (sfero/skali v-bob monto)
                          transfero (sfero/kunigo [v-debito v-kredito])]
                      (if (registro/provi-transakcion v-alice monto 
                            (fn []
                              (let [nonce (registro/mini-vektoron transfero)] 
                                (registro/ĉu-nova-transakcio? transfero nonce)
                                (registro/aldoni-transakcion transfero)
                                (doseq [p peers] (reto/sendi-transakcion p transfero nonce)))))
                        (println ">>> ÉXITO: Transferencia minada y enviada.")
                        (println ">>> ERROR: Fondos insuficientes.")))

                "0" (System/exit 0)
                (println "Opción no reconocida."))
              (recur))))))))
