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
        monero (sfero/naski-vektoron 999)
        tasa-comision 0.05] ;; 5% de comisión

    ;; 1. Bob empieza con fondos
    (registro/aldoni-transakcion (sfero/ligi monero (:id-vektoro bob)))
    
    (println "\n--- Transferencia con Comisión (Kotizo) ---")
    (let [v-total (sfero/ligi monero (:id-vektoro bob))
          
          ;; El gasto de Bob (el 100%)
          debito-bob (sfero/inversi v-total)
          
          ;; Lo que recibe Alice (el 95%)
          kredito-alice (sfero/skali (sfero/ligi monero (:id-vektoro alice)) (- 1.0 tasa-comision))
          
          ;; Lo que recibe la Red (el 5%) - Usamos el mismo vector de Alice pero ligado a la Red
          v-infra (sfero/naski-vektoron 777) ;; Vector identificador de infraestructura
          kotizo (sfero/skali (sfero/ligi monero v-infra) tasa-comision)]
      
      ;; Registramos la transferencia y la comisión
      (registro/aldoni-transakcion (sfero/kunigo [debito-bob kredito-alice]))
      (registro/aldoni-kotizon kotizo)

      (println (format "Saldo Final Bob:   %.4f SFE" (registro/kalkuli-ekvilibron (sfero/ligi monero (:id-vektoro bob)))))
      (println (format "Saldo Final Alice: %.4f SFE" (registro/kalkuli-ekvilibron (sfero/ligi monero (:id-vektoro alice)))))
      (println (format "Fondo Red (Infra): %.4f SFE" (sfero/resonanco (sfero/ligi monero v-infra) kotizo))))

    (println "====================================")))
