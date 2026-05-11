(ns sfero.node
  (:require [sfero.core :as sfero]
            [sfero.monujo :as monujo]))

(defn -main [& args]
  (println "====================================")
  (println "      SFERO NETWORK - v0.1.0       ")
  (println "====================================")
  
  ;; 1. Crear billetera del usuario
  (let [mia-monujo (monujo/krei-monujon "mia sekreta pasvorto")
        id-v (:id-vektoro mia-monujo)
        adreso (:publika-adreso mia-monujo)]
    
    (println "Billetera generada.")
    (println "Dirección pública (primeros 5 valores):" (take 5 adreso))
    
    ;; 2. Crear una transacción (ej: "pago 10")
    (let [alfabeto (let [base (sfero/naski-vektoron 55555)]
                     (into {} (map (fn [c] [c (sfero/permutado base (int c))]) 
                                   "pago 10")))
          transakcio (sfero/encode-text "pago 10" alfabeto)
          
          ;; 3. Firmar la transacción vinculándola a la identidad
          subskribo (monujo/subskribi transakcio id-v)]
      
      (println "Transacción 'pago 10' firmada con éxito.")
      
      ;; 4. Verificación de seguridad (Resonancia)
      ;; Si intentamos verificar con la identidad correcta, la resonancia será alta.
      (let [verigo (sfero/resonanco (sfero/ligi subskribo id-v) transakcio)]
        (println "Verificación de Firma (Resonancia):" verigo)
        (if (> verigo 0.5)
          (println "RESULTADO: Firma Válida. Identidad confirmada.")
          (println "RESULTADO: Firma INVÁLIDA."))))))
