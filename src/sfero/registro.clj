(ns sfero.registro
  (:require [sfero.core :as sfero]
            [sfero.konservado :as konservado]))

;; --- CONFIGURACIÓN ---
(def ^:const saturigo-limo 10)   
(def ^:const persisteco 0.98)
(def malfacileco 4)

;; Cargamos el estado inicial. 
;; Incluimos :nonces para que la red "recuerde" lo visto tras reiniciar.
(defonce ^:private cxeno   
  (atom (or (konservado/sxargi-cxenon sfero/dim)
            {:arhivo []   
             :viva (vec (repeat sfero/dim 0.0))
             :kalkulilo 0
             :nonces #{}}))) 

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

;; --- ESCRITURA Y PERSISTENCIA ---
(defn aldoni-transakcion [subskribo nonce]
  (swap! cxeno (fn [s]
                 (let [h (sfero/haŝi-transakcion subskribo nonce)
                       nova-viva (mapv + (sfero/malkresko (:viva s) persisteco) subskribo)
                       ;; Usamos (fnil conj #{}) para asegurar que siempre sea un Set
                       estado-updated (-> s   
                                          (assoc :viva nova-viva)
                                          (update :kalkulilo inc)
                                          (update :nonces (fnil conj #{}) h))]
                   
                   (if (>= (:kalkulilo estado-updated) saturigo-limo)
                     (do (println "SFERO > Cristalizando y sincronizando binario...")
                         (kristaligi estado-updated))
                     (do 
                       (konservado/konservi-cxenon estado-updated)
                       estado-updated)))))
  nil)

;; --- REPORTES Y SALDOS ---
(defn kalkuli-ekvilibron [identeco-vektoro]
  (let [{:keys [viva arhivo]} @cxeno
        res-viva (sfero/resonanco viva identeco-vektoro)
        res-arhivo (reduce + 0.0 (map (fn [bloque]   
                                       (* (sfero/resonanco (:v bloque) identeco-vektoro)   
                                          (:pezo bloque)))   
                                     arhivo))]
    (+ res-viva res-arhivo)))

;; --- VALIDACIÓN Y MINERÍA ---

(defn ĉu-nova-transakcio? [vektoro nonce]
  (let [h (sfero/haŝi-transakcion vektoro nonce)
        ;; Nos aseguramos de que si :nonces no existe, usemos un Set vacío #{}
        nonces (get @cxeno :nonces #{})]
    (not (contains? nonces h))))

(defn valida-minado? [vektoro nonce]
  (let [hasxo (sfero/haŝi-transakcion vektoro nonce)
        celo (apply str (repeat malfacileco "0"))]
    (.startsWith hasxo celo)))

(defn mini-vektoron [vektoro]
  (println "MINADO > Buscando resonancia estable (Dificultad:" malfacileco ")...")
  (loop [n (rand-int 1000000000)]
    (if (valida-minado? vektoro n)
      (do (println "MINADO > ¡Encontrado! Nonce:" n) n)
      (recur (inc n)))))

;; --- SEGURIDAD ---

(defn provi-transakcion [id-sendinto valoro-transakcio f-transakcio]
  (let [saldo (kalkuli-ekvilibron id-sendinto)]
    (if (>= saldo valoro-transakcio)
      (do (f-transakcio) 
          (println (format "GARDA > Transacción autorizada. Saldo: %.4f SFE" saldo))
          true)
      (do (println (format "GARDA > Fondos insuficientes: %.4f" saldo))
          false))))

(defn provi-multisig [id-komuna id-subskribo valoro f-transakcio]
  (let [akordo (sfero/resonanco id-komuna id-subskribo)]
    (if (>= akordo 0.9)
      (do (f-transakcio)
          (println (format "GARDA > Multifirma aceptada (Resonancia: %.2f)" (double akordo)))
          true)
      (do (println "GARDA > Firma insuficiente.")
          false))))
