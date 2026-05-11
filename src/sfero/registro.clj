(ns sfero.registro
  (:require [sfero.core :as sfero]))

;; --- CONFIGURACIÓN ---
(def ^:const saturigo-limo 100) 
(def ^:const persisteco 0.98)

;; Solo un átomo para gobernarlos a todos
(defonce ^:private cxeno (atom {:arhivo [] 
                                :viva (vec (repeat sfero/dim 0.0))
                                :kalkulilo 0}))

(defn- kristaligi [stato]
  (let [vektoro-kristaligita (sfero/binarigi (:viva stato))]
    (-> stato
        (update :arhivo conj vektoro-kristaligita)
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
                     (do (println "SFERO > Cristalizando bloque histórico...")
                         (kristaligi stato-updated))
                     stato-updated))))
  (println "SFERO > Registro actualizado."))

;; --- LECTURA ---
(defn vidi-staton-totala []
  "Devuelve la suma del vector vivo y todo el historial cristalizado."
  (let [{:keys [viva arhivo]} @cxeno]
    (reduce (fn [acc v] (mapv + acc v)) viva arhivo)))

(defn kalkuli-ekvilibron [identeco-vektoro]
  "Calcula el saldo sumando la resonancia en toda la cadena."
  (let [total (vidi-staton-totala)
        res (sfero/resonanco total identeco-vektoro)]
    (max 0.0 res)))

;; --- INFRAESTRUCTURA ---
(defonce ^:private infra-strukturo (atom (vec (repeat sfero/dim 0.0))))
(defn aldoni-kotizon [kotizo-vektoro] (swap! infra-strukturo #(mapv + % kotizo-vektoro)))
