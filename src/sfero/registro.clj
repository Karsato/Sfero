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

(defn vidi-staton []
  "Permite que el explorador lea el estado actual del registro."
  @stato-globala)

(defn kalkuli-ekvilibron [identeco-vektoro]
  (let [res (sfero/resonanco (vidi-staton) identeco-vektoro)]
    (max 0.0 (- res 0.05))))

(defn cxu-ekzistas? [subskribo]
  (let [res (sfero/resonanco @stato-globala subskribo)]
    (println (format "SFERO > Resonancia actual: %.4f" (double res)))
    res))

(defn kalkuli-ekvilibron [identeco-vektoro]
  "Calcula cuánto resuena una identidad específica en el registro global.
   Ese valor de resonancia representa el poder adquisitivo (saldo)."
  (let [res (sfero/resonanco @stato-globala identeco-vektoro)]
    ;; Limpiamos el ruido base (ajuste estadístico)
    (max 0.0 (- res 0.05))))

(defonce ^:private infra-strukturo (atom (vec (repeat sfero/dim 0.0))))

(defn aldoni-kotizon [kotizo-vektoro]
  "Acumula la comisión en el vector de infraestructura."
  (swap! infra-strukturo (fn [s] (mapv + s kotizo-vektoro))))

(defn vidi-infra-staton []
  "Muestra cuánta energía hay acumulada para los mineros."
  (sfero/resonanco @infra-strukturo (vec (repeat sfero/dim 1.0)))) ; Medimos energía neta
