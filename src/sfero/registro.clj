(ns sfero.registro
  (:require [sfero.core :as sfero]))

;; --- CONFIGURACIÓN ---
(def ^:const saturigo-limo 10) 
(def ^:const persisteco 0.98)

;; Solo un átomo para gobernarlos a todos
(defonce ^:private cxeno (atom {:arhivo [] 
                                :viva (vec (repeat sfero/dim 0.0))
                                :kalkulilo 0}))

(defn- kristaligi [stato]
  "Cristaliza guardando el vector binarizado pero recordando su peso (energía)."
  (let [peso-actual (:kalkulilo stato)
        vektoro-kristaligita (sfero/binarigi (:viva stato))]
    (-> stato
        ;; Guardamos el vector Y su peso
        (update :arhivo conj {:v vektoro-kristaligita :pezo peso-actual})
        (assoc :viva (vec (repeat sfero/dim 0.0)))
        (assoc :kalkulilo 0))))

;; --- ESCRITURA ---
(defn aldoni-transakcion [subskribo]
  (swap! cxeno (fn [s]
                 (let [nova-viva (mapv + (sfero/malkresko (:viva s) persisteco) subskribo)
                       stato-updated (-> s 
                                         (assoc :viva nova-viva)
                                         (update :kalkulilo inc))]
                   (if (>= (:kalkulilo stato-updated) saturigo-limo)
                     (kristaligi stato-updated)
                     stato-updated))))
  (println "SFERO > Registro actualizado."))

;; --- FUNCIONES PÚBLICAS (LECTURA/REPORTES) ---
(defn arkivo-grandeco [] (count (:arhivo @cxeno)))
(defn viva-kalkulilo [] (:kalkulilo @cxeno))

;; --- LECTURA ---
(defn vidi-staton-totala []
  "Devuelve la suma del vector vivo y todo el historial cristalizado."
  (let [{:keys [viva arhivo]} @cxeno]
    (reduce (fn [acc v] (mapv + acc v)) viva arhivo)))

(defn kalkuli-ekvilibron [identeco-vektoro]
  (let [{:keys [viva arhivo]} @cxeno
        ;; Resonancia del vector que aún no se ha congelado
        res-viva (sfero/resonanco viva identeco-vektoro)
        ;; Resonancia de los bloques congelados multiplicada por su peso
        res-arhivo (reduce + 0.0 (map (fn [bloque] 
                                       (* (sfero/resonanco (:v bloque) identeco-vektoro) 
                                          (:pezo bloque))) 
                                     arhivo))]
    (+ res-viva res-arhivo)))

;; --- INFRAESTRUCTURA ---
(defonce ^:private infra-strukturo (atom (vec (repeat sfero/dim 0.0))))
(defn aldoni-kotizon [kotizo-vektoro] (swap! infra-strukturo #(mapv + % kotizo-vektoro)))
