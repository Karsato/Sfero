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

(defn provi-transakcion [id-sendinto valoro-transakcio f-transakcio]
  "Verifica si el remitente tiene suficiente resonancia antes de aplicar la función f-transakcio."
  (let [saldo (kalkuli-ekvilibron id-sendinto)]
    (if (>= saldo valoro-transakcio)
      (do
        (f-transakcio) ;; Ejecutamos la integración (aldoni-transakcion)
        (println (format "GARDA > Transacción autorizada. Saldo suficiente: %.4f SFE" saldo))
        true)
      (do
        (println (format "GARDA > ALERTA: Fondos insuficientes. Saldo: %.4f | Requerido: %.4f" 
                         saldo valoro-transakcio))
        false))))

(defonce ^:private vidi-listo (atom #{}))

(defn ĉu-nova? [vektoro]
  "Verifica si el vector es nuevo. Si lo es, guarda su hash y devuelve true."
  (let [h (sfero/haŝi-vektoron vektoro)]
    (if (contains? @vidi-listo h)
      false
      (do 
        (swap! vidi-listo conj h)
        true))))
