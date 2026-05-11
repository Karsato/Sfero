(ns sfero.node
  (:require [sfero.core :as sfero]
            [sfero.monujo :as monujo]
            [sfero.registro :as registro]))

(defn -main [& args]
  (println "====================================")
  (println "      SFERO NETWORK - v0.1.0       ")
  (println "====================================")
  
  (let [alfabeto (let [base (sfero/naski-vektoron 55555)]
                   (into {} (map (fn [c] [c (sfero/permutado base (int c))]) 
                                 "abcdefghijklmnopqrstuvwxyz 0123456789")))
        alice (monujo/krei-monujon "clave-secreta-alice")
        pago-v (sfero/kodigi-tekston "pago inicial" alfabeto)
        firma-alice (monujo/subskribi pago-v (:id-vektoro alice))]

    ;; T0: Añadimos la primera transacción
    (println "\n--- T0: Primera transacción ---")
    (registro/aldoni-transakcion firma-alice)
    (let [res0 (registro/cxu-ekzistas? firma-alice)]
      (println "Resonancia inicial:" res0))

    ;; T1...T5: Simulamos que pasa el tiempo añadiendo ruido (otras transacciones)
    (println "\n--- Simulando el paso del tiempo (5 ciclos) ---")
    (doseq [i (range 1 6)]
      (let [ruido (sfero/naski-vektoron i)] ;; Simulamos otras firmas
        (registro/aldoni-transakcion ruido)))

    ;; Verificación final
    (println "\n--- Verificación tras decaimiento ---")
    (let [res-final (registro/cxu-ekzistas? firma-alice)]
      (println "Resonancia actual:" res-final)
      (if (< res-final 10.0) ;; El valor habrá bajado de los 16 originales
        (println ">>> EL TIEMPO HA PASADO: La transacción es ahora una memoria lejana.")
        (println ">>> ERROR: El decaimiento no funcionó.")))
    
  (println "====================================")))
