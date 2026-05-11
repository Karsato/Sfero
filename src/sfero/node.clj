(ns sfero.node
  (:require [sfero.core :as sfero]
            [sfero.monujo :as monujo]
            [sfero.registro :as registro]))

(defn -main [& args]
  (println "====================================")
  (println "      SFERO NETWORK - v0.1.0       ")
  (println "   Intercambio Hiperdimensional     ")
  (println "====================================")
  
  (let [alice (monujo/krei-monujon "alice")
        bob   (monujo/krei-monujon "bob")
        monero (sfero/naski-vektoron 999)]

    (println "--- Estado Inicial (Cargado de disco) ---")
    (let [s-alice (registro/kalkuli-ekvilibron (sfero/ligi monero (:id-vektoro alice)))
          s-bob   (registro/kalkuli-ekvilibron (sfero/ligi monero (:id-vektoro bob)))]
      (println (format "Alice: %.4f SFE" s-alice))
      (println (format "Bob:   %.4f SFE" s-bob)))

    (println "\n--- Acción: Alice envía 10 SFE a Bob ---")
    ;; Creamos el vector de transferencia (Débito Alice + Crédito Bob)
    ;; Escalamos el vector por 10 para transferir 10 unidades de fuerza
    (let [v-debito  (sfero/skali (sfero/inversi (sfero/ligi monero (:id-vektoro alice))) 10.0)
          v-kredito (sfero/skali (sfero/ligi monero (:id-vektoro bob)) 10.0)
          transfero (sfero/kunigo [v-debito v-kredito])]
      
      (registro/aldoni-transakcion transfero))

    (println "\n--- Estado Final ---")
    (let [s-alice-f (registro/kalkuli-ekvilibron (sfero/ligi monero (:id-vektoro alice)))
          s-bob-f   (registro/kalkuli-ekvilibron (sfero/ligi monero (:id-vektoro bob)))]
      (println (format "Alice: %.4f SFE" s-alice-f))
      (println (format "Bob:   %.4f SFE" s-bob-f))
      
      (println "\n>>> Masa Total de la Red:" (+ s-alice-f s-bob-f) "SFE"))))
