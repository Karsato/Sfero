(ns sfero.node
  (:require [sfero.core :as sfero]
            [sfero.monujo :as monujo]
            [sfero.registro :as registro]))

(defn -main [& args]
  (println "====================================")
  (println "      SFERO NETWORK - v0.1.0       ")
  (println "      Prueba: Garda-Vektoro        ")
  (println "====================================")
  
  (let [alice (monujo/krei-monujon "alice")
        bob   (monujo/krei-monujon "bob")
        monero (sfero/naski-vektoron 999)
        id-alice (sfero/ligi monero (:id-vektoro alice))
        id-bob   (sfero/ligi monero (:id-vektoro bob))]

    (println "--- Situación Actual ---")
    (println (format "Saldo de Alice: %.4f SFE" (registro/kalkuli-ekvilibron id-alice)))

    (println "\n--- Intento 1: Alice envía 5 SFE (Legal) ---")
    (let [monto 5.0
          pago (sfero/kunigo [(sfero/skali (sfero/inversi id-alice) monto)
                              (sfero/skali id-bob monto)])]
      (registro/provi-transakcion id-alice monto #(registro/aldoni-transakcion pago)))

    (println "\n--- Intento 2: Alice intenta enviar 100 SFE (Fraude) ---")
    (let [monto 100.0
          fraude (sfero/kunigo [(sfero/skali (sfero/inversi id-alice) monto)
                                (sfero/skali id-bob monto)])]
      (if (registro/provi-transakcion id-alice monto #(registro/aldoni-transakcion fraude))
        (println ">>> ERROR: El sistema permitió un gasto excesivo.")
        (println ">>> ÉXITO: El Garda-Vektoro detuvo el fraude.")))

    (println "\n--- Saldo Final de Alice ---")
    (println (format "%.4f SFE" (registro/kalkuli-ekvilibron id-alice))))
  
  (println "===================================="))
