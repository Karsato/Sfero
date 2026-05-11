(ns sfero.node
  (:require [sfero.core :as sfero]))

(defn -main [& args]
  (println "====================================")
  (println "      SFERO NETWORK - v0.1.0       ")
  (println "   Hyperdimensional Economy Active  ")
  (println "====================================")
  
  (let [semilla-red 55555
        ;; Creamos el alfabeto base de la red (Item Memory)
        alfabeto (let [base (sfero/naski-vektoron semilla-red)]
                   (into {} (map (fn [c] [c (sfero/permutado base (int c))]) 
                                 "abcdefghijklmnopqrstuvwxyz ")))
        
        ;; EL VECTOR GÉNESIS: "sfero estas libera"
        genesis-text "sfero estas libera"
        genesis-vector (sfero/kunigo 
                         (map-indexed (fn [idx c] 
                                        (sfero/permutado (get alfabeto c) idx)) 
                                      genesis-text))]
    
    (println "Génesis Vector generado exitosamente.")
    (println "Similitud con 's' en pos 0:" 
             (sfero/resonanco (sfero/permutado genesis-vector 0) (get alfabeto \s)))
    (println "------------------------------------")))
