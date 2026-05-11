(ns sfero.registro
  (:require [sfero.core :as sfero]
            [sfero.konservado :as konservado]))

;; --- CONFIGURACIÓN ---
(def ^:const saturigo-limo 10) 
(def ^:const persisteco 0.98)

;; Cargamos el estado inicial (Pasando sfero/dim para el lector binario)
(defonce ^:private cxeno 
  (atom (or (konservado/sxargi-cxenon sfero/dim)
            {:arhivo [] 
             :viva (vec (repeat sfero/dim 0.0))
             :kalkulilo 0})))

;; --- LÓGICA DE CRISTALIZACIÓN ---
(defn- kristaligi [stato]
  (let [peso-actual (:kalkulilo stato)
        vektoro-kristaligita (sfero/binarigi (:viva stato))
        nova-stato (-> stato
                       (update :arhivo conj {:v vektoro-kristaligita :pezo peso-actual})
                       (assoc :viva (vec (repeat sfero/dim 0.0)))
                       (assoc :kalkulilo 0))]
    (konservado/konservi-cxenon nova-stato)
    nova-stato))

;; --- ESCRITURA ---
(defn aldoni-transakcion [subskribo]
  (swap! cxeno (fn [s]
                 (let [nova-viva (mapv + (sfero/malkresko (:viva s) persisteco) subskribo)
                       stato-updated (-> s 
                                         (assoc :viva nova-viva)
                                         (update :kalkulilo inc))]
                   (if (>= (:kalkulilo stato-updated) saturigo-limo)
                     (do (println "SFERO > Cristalizando y sincronizando binario...")
                         (kristaligi stato-updated))
                     stato-updated))))
  nil)

;; --- REPORTES Y SALDOS ---
(defn arkivo-grandeco [] (count (:arhivo @cxeno)))
(defn viva-kalkulilo [] (:kalkulilo @cxeno))

(defn kalkuli-ekvilibron [identeco-vektoro]
  (let [{:keys [viva arhivo]} @cxeno
        res-viva (sfero/resonanco viva identeco-vektoro)
        res-arhivo (reduce + 0.0 (map (fn [bloque] 
                                       (* (sfero/resonanco (:v bloque) identeco-vektoro) 
                                          (:pezo bloque))) 
                                     arhivo))]
    (+ res-viva res-arhivo)))

(defn vidi-staton-totala []
  (let [{:keys [viva arhivo]} @cxeno]
    (reduce (fn [acc b] (mapv + acc (:v b))) viva arhivo)))
