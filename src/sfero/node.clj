(ns sfero.node
  (:require [sfero.core :as sfero]
            [sfero.monujo :as monujo]
            [sfero.registro :as registro]))

(defn -main [& args]
  (println "====================================")
  (println "      SFERO NETWORK - v0.1.0       ")
  (println "====================================")
  
  (let [alice (monujo/krei-monujon "alice")
        bob (monujo/krei-monujon "bob")
        carol (monujo/krei-monujon "carol")
        monero (sfero/naski-vektoron 999)
        
        ;; Diccionario para el explorador
        vortaro {"Alice" (:id-vektoro alice)
                 "Bob"   (:id-vektoro bob)
                 "Carol" (:id-vektoro carol)}]

    (println "--- Generando actividad en la red ---")
    ;; Alice recibe 3 pagos, Bob 1, Carol 0
    (dotimes [_ 3] (registro/aldoni-transakcion (sfero/ligi monero (:id-vektoro alice))))
    (registro/aldoni-transakcion (sfero/ligi monero (:id-vektoro bob)))

    (println "\n--- SFERO ESPLORILO (Explorador de Hiperespacio) ---")
    (println "Escaneando resonancias de identidad...")
    
    ;; Extraemos el estado actual del registro
    (let [stato (registro/vidi-staton) 
          rezultoj (sfero/skani-registron stato 
                     (into {} (map (fn [[n v]] [n (sfero/ligi monero v)]) vortaro)))]
      
      (doseq [[nomo valoro] rezultoj]
        (let [bar-length (int (* valoro 10))]
          (println (format "%-10s [%-15s] %.4f SFE" 
                           nomo 
                           (apply str (repeat (max 0 bar-length) "█")) 
                           (double valoro)))))

      (println "\n>>> Análisis: El hiperespacio muestra una clara dominancia de Alice.")))
  
  (println "===================================="))
