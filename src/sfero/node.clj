(ns sfero.node
  (:require [sfero.core :as sfero]
            [sfero.monujo :as monujo]
            [sfero.registro :as registro]))

(defn -main [& args]
  (println "====================================")
  (println "      SFERO NETWORK - v0.1.0       ")
  (println "====================================")
  
  (let [alice (monujo/krei-monujon "alice-seed")
        bob (monujo/krei-monujon "bob-seed")
        
        ;; Definimos el activo: "SFERO-COIN"
        monero (sfero/naski-vektoron 999)]

    (println "Situación inicial: Alice y Bob tienen saldo 0.")
    
    ;; 1. Bob recibe dos depósitos (Vinculamos el activo a su ID)
    (println "\n--- Bob recibe 2 depósitos de la Red ---")
    (let [deposito-1 (sfero/ligi monero (:id-vektoro bob))
          deposito-2 (sfero/ligi monero (:id-vektoro bob))]
      (registro/aldoni-transakcion deposito-1)
      (registro/aldoni-transakcion deposito-2))

    ;; 2. Consultamos el saldo (Ekvilibro)
    ;; Para consultar, ligamos el activo al registro y vemos cuánto resuena con el ID
    (println "\n--- Consultando Saldos en el Hiperespacio ---")
    
    (let [saldo-bob (registro/kalkuli-ekvilibron (sfero/ligi monero (:id-vektoro bob)))
          saldo-alice (registro/kalkuli-ekvilibron (sfero/ligi monero (:id-vektoro alice)))]
      
      (println (format "Saldo de Bob:   %.4f SFE" saldo-bob))
      (println (format "Saldo de Alice: %.4f SFE" saldo-alice))

      (if (> saldo-bob saldo-alice)
        (println "\n>>> ÉXITO: El hiperespacio reconoce que Bob es más rico.")
        (println "\n>>> ERROR: La simetría no se detectó."))))
  
  (println "===================================="))
