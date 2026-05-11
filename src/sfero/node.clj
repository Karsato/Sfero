(ns sfero.node
  (:require [sfero.core :as sfero]
            [sfero.monujo :as monujo]
            [sfero.registro :as registro]))

(defn -main [& args]
  (println "====================================")
  (println "      SFERO NETWORK - v0.1.0       ")
  (println "   Test de Cristalización Masiva    ")
  (println "====================================")
  
  (let [alice (monujo/krei-monujon "alice")
        monero (sfero/naski-vektoron 999)
        total-transakcioj 25]

    (println "--- Iniciando ráfaga de 25 transacciones para Alice ---")
    
    (dotimes [n total-transakcioj]
      (let [pago (sfero/ligi monero (:id-vektoro alice))]
        (registro/aldoni-transakcion pago)
        ;; Cada 5 transacciones, hacemos un reporte de estado
        (when (zero? (mod (inc n) 5))
          (println (format ">> Progreso: %d/%d | Bloques en archivo: %d | En vivo: %d" 
                           (inc n) total-transakcioj 
                           (registro/arkivo-grandeco)
                           (registro/viva-kalkulilo))))))

    (println "\n--- ESTADO FINAL DE LA RED ---")
    (println "Bloques Cristalizados (Archivo):" (registro/arkivo-grandeco))
    (println "Transacciones en Vector Vivo:  " (registro/viva-kalkulilo))
    
    (println "\n--- VERIFICACIÓN DE SALDO ---")
    (let [saldo-final (registro/kalkuli-ekvilibron (sfero/ligi monero (:id-vektoro alice)))]
      (println (format "Saldo total de Alice (Pasado + Presente): %.4f SFE" (double saldo-final)))
      
      (if (> saldo-final 20.0)
        (println ">>> ÉXITO: El valor persiste a través de la cristalización.")
        (println ">>> ERROR: Se ha perdido energía en el proceso.")))
    
    (println "====================================")))
