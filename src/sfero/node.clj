(ns sfero.node
  (:require [sfero.core :as sfero]
            [sfero.monujo :as monujo]))

(defn -main [& args]
  (println "====================================")
  (println "      SFERO NETWORK - v0.1.0       ")
  (println "====================================")
  
  (let [mia-monujo (monujo/krei-monujon "mia sekreta pasvorto")
        id-v (:id-vektoro mia-monujo)]
    
    (println "Billetera Sfero generada con éxito.")
    
    ;; Creamos un alfabeto rápido para la prueba
    (let [alfabeto (let [base (sfero/naski-vektoron 55555)]
                     (into {} (map (fn [c] [c (sfero/permutado base (int c))]) 
                                   "pago 10")))
          
          ;; Usamos el nombre correcto: kodigi-tekston
          transakcio (sfero/kodigi-tekston "pago 10" alfabeto)
          
          ;; Firmamos
          subskribo (monujo/subskribi transakcio id-v)]
      
      (println "Transacción 'pago 10' firmada.")
      
      (let [verigo (sfero/resonanco (sfero/ligi subskribo id-v) transakcio)]
        (println "Resonancia de Verificación:" verigo)
        (if (> verigo 0.5)
          (println ">>> FIRMA VÁLIDA: Acceso al hiperespacio concedido.")
          (println ">>> ERROR: Firma ilegítima."))))))
