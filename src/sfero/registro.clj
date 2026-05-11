(ns sfero.registro
  (:require [sfero.core :as sfero]))

(defonce ^:private stato-globala (atom (vec (repeat sfero/dim 0.0))))

;; Factor de persistencia (cuánto sobrevive del pasado)
(def ^:const persisteco 0.9) 

(defn aldoni-transakcion [subskribo]
  "Aplica decaimiento al estado actual y luego integra la nueva firma."
  (swap! stato-globala 
         (fn [s] 
           (let [malnova-stato (sfero/malkresko s persisteco)]
             (mapv + malnova-stato subskribo))))
  (println "SFERO > Registro actualizado (Decaimiento aplicado)."))

(defn cxu-ekzistas? [subskribo]
  (let [res (sfero/resonanco @stato-globala subskribo)]
    (println (format "SFERO > Resonancia actual: %.4f" (double res)))
    res))
