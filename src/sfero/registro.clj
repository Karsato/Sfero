(ns sfero.registro
  (:require [sfero.core :as sfero]
            [sfero.konservado :as konservado]
            [clojure.java.io :as io]))

;; --- CONFIGURACIÓN ---
(def ^:const saturigo-limo 10)     
(def ^:const persisteco 0.98)
(def ^:const malfacileco 4)
(def ^:const bloques-por-archivo 5)

;; 1. Definimos primero el estado (Persistente)
(defonce ^:private cxeno     
  (atom (or (konservado/sxargi-cxenon sfero/dim)
            {:arhivo []     
             :viva (vec (repeat sfero/dim 0.0))
             :kalkulilo 0
             :nonces #{}}))) 

;; 2. Utilidades
(defn viva-kalkulilo [] (:kalkulilo @cxeno))

(defn- obtener-siguiente-indice []
  (let [archivos (filter #(re-matches #"sfero_cxeno\.\d+\.bin" (.getName %)) 
                         (.listFiles (io/file ".")))]
    (inc (count archivos))))

;; 3. Lógica de Cristalización
(defn- kristaligi [stato]
  (let [peso-actual (:kalkulilo stato)
        vektoro-kristaligita (sfero/binarigi (:viva stato))
        arhivo-actual (conj (:arhivo stato) {:v vektoro-kristaligita :pezo peso-actual})]
    (if (>= (count arhivo-actual) bloques-por-archivo)
      (let [n-idx (obtener-siguiente-indice)
            nombre-frio (str "sfero_cxeno." n-idx ".bin")]
        (println "SFERO > Partición llena. Rotando a" nombre-frio)
        (konservado/konservi-cxenon {:arhivo arhivo-actual} nombre-frio)
        (let [nova-stato (assoc stato :arhivo [] :viva (vec (repeat sfero/dim 0.0)) :kalkulilo 0)]
          (konservado/konservi-cxenon nova-stato)
          nova-stato))
      (let [nova-stato (assoc stato :arhivo arhivo-actual :viva (vec (repeat sfero/dim 0.0)) :kalkulilo 0)]
        (konservado/konservi-cxenon nova-stato)
        nova-stato))))

;; 4. Escritura
(defn aldoni-transakcion [subskribo nonce]
  (swap! cxeno (fn [s]
                 (let [h (sfero/haŝi-transakcion subskribo nonce)
                       nova-viva (mapv + (sfero/malkresko (:viva s) persisteco) subskribo)
                       estado-updated (-> s   
                                          (assoc :viva nova-viva)
                                          (update :kalkulilo inc)
                                          (update :nonces (fnil conj #{}) h))]
                   (if (>= (:kalkulilo estado-updated) saturigo-limo)
                     (do (println "SFERO > Cristalizando y sincronizando...")
                         (kristaligi estado-updated))
                     (do (konservado/konservi-cxenon estado-updated)
                         estado-updated)))))
  nil)

;; 5. Reportes y Validación
(defn kalkuli-ekvilibron [id-vektoro]
  (let [archivos (filter #(re-matches #"sfero_cxeno\.\d+\.bin" (.getName %)) (.listFiles (io/file ".")))
        res-hist (reduce + 0.0 (for [f archivos]
                                 (let [d (konservado/sxargi-cxenon-el-dosiero (.getName f) sfero/dim)]
                                   (reduce + 0.0 (map #(* (sfero/resonanco (:v %) id-vektoro) (:pezo %)) (:arhivo d))))))
        {:keys [viva arhivo]} @cxeno]
    (+ res-hist (sfero/resonanco viva id-vektoro) 
       (reduce + 0.0 (map #(* (sfero/resonanco (:v %) id-vektoro) (:pezo %)) arhivo)))))

(defn ĉu-nova-transakcio? [vektoro nonce]
  (not (contains? (get @cxeno :nonces #{}) (sfero/haŝi-transakcion vektoro nonce))))

(defn valida-minado? [vektoro nonce]
  (.startsWith (sfero/haŝi-transakcion vektoro nonce) (apply str (repeat malfacileco "0"))))

(defn mini-vektoron [vektoro]
  (println "MINADO > Buscando resonancia...")
  (loop [n (rand-int 1000000000)]
    (if (valida-minado? vektoro n) n (recur (inc n)))))

(defn provi-transakcion [id-sendinto valoro f-transakcio]
  (let [saldo (kalkuli-ekvilibron id-sendinto)]
    (if (>= saldo valoro) (do (f-transakcio) true) (do (println "GARDA > Saldo insuficiente:" saldo) false))))

(defn provi-multisig [id-komuna id-subskribo valoro f-transakcio]
  (if (>= (sfero/resonanco id-komuna id-subskribo) 0.9) (do (f-transakcio) true) false))
