(ns sfero.registro
  (:require [sfero.core :as sfero]
            [sfero.konservado :as konservado]
            [clojure.java.io :as io]))

;; --- CONFIGURACIÓN ---
(def ^:const saturigo-limo 10)   
(def ^:const persisteco 0.98)
(def malfacileco 4)

;; --- NUEVA CONFIGURACIÓN EN registro.clj ---
(def ^:const particion-limo 5) ;; Cada 5 cristales, creamos un archivo nuevo

(defn- generi-nomon-particion [n]
  (format "sfero_cxeno.%04d.bin" n))

(defn- trovi-lastan-particion []
  (let [archivos (filter #(.startsWith (.getName %) "sfero_cxeno.") (.listFiles (clojure.java.io/file ".")))]
    (count archivos)))

;; --- CONFIGURACIÓN DE PARTICIONES ---
(def ^:const bloques-por-archivo 5) ;; Cada 5 cristales (50 txs), rotamos archivo

(defn- obtener-siguiente-indice []
  (let [archivos (filter #(re-matches #"sfero_cxeno\.\d+\.bin" (.getName %)) 
                         (.listFiles (clojure.java.io/file ".")))]
    (inc (count archivos))))


;; Cargamos el estado inicial. 
;; Incluimos :nonces para que la red "recuerde" lo visto tras reiniciar.
(defonce ^:private cxeno   
  (atom (or (konservado/sxargi-cxenon sfero/dim)
            {:arhivo []   
             :viva (vec (repeat sfero/dim 0.0))
             :kalkulilo 0
             :nonces #{}}))) 

;; --- LÓGICA DE CRISTALIZACIÓN CON ROTACIÓN ---
(defn- kristaligi [stato]
  (let [peso-actual (:kalkulilo stato)
        vektoro-kristaligita (sfero/binarigi (:viva stato))
        arhivo-actual (conj (:arhivo stato) {:v vektoro-kristaligita :pezo peso-actual})]
    
    (if (>= (count arhivo-actual) bloques-por-archivo)
      (let [n-idx (obtener-siguiente-indice)
            nombre-frio (str "sfero_cxeno." n-idx ".bin")]
        (println "SFERO > Partición llena. Rotando a" nombre-frio)
        ;; Guardamos el archivo "frío" con los bloques acumulados
        (konservado/konservi-cxenon {:arhivo arhivo-actual} nombre-frio)
        ;; Reiniciamos el estado caliente (vacío)
        (let [nova-stato (assoc stato :arhivo [] :viva (vec (repeat sfero/dim 0.0)) :kalkulilo 0)]
          (konservado/konservi-cxenon nova-stato)
          nova-stato))
      ;; Si no hemos llegado al límite, guardamos en el archivo caliente normal
      (let [nova-stato (assoc stato :arhivo arhivo-actual :viva (vec (repeat sfero/dim 0.0)) :kalkulilo 0)]
        (konservado/konservi-cxenon nova-stato)
        nova-stato))))

;; --- ESCRITURA Y PERSISTENCIA ---
(defn aldoni-transakcion [subskribo nonce]
  (swap! cxeno (fn [s]
                 (let [h (sfero/haŝi-transakcion subskribo nonce)
                       nova-viva (mapv + (sfero/malkresko (:viva s) persisteco) subskribo)
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
  (let [archivos-directorio (.listFiles (io/file "."))
        ;; Buscamos archivos que sigan el patrón sfero_cxeno.N.bin
        archivos-particion (filter #(re-matches #"sfero_cxeno\.\d+\.bin" (.getName %)) 
                                   archivos-directorio)
        
        ;; 1. Calculamos resonancia en las particiones frías (históricas)
        res-historica (reduce + 0.0 
                        (for [f archivos-particion]
                          (let [datos (konservado/sxargi-cxenon-el-dosiero (.getName f) sfero/dim)]
                            (reduce + 0.0 
                              (map (fn [b] (* (sfero/resonanco (:v b) identeco-vektoro) (:pezo b)))
                                   (:arhivo datos))))))
        
        ;; 2. Calculamos resonancia en el estado actual (viva + arhivo caliente)
        {:keys [viva arhivo]} @cxeno
        res-viva (sfero/resonanco viva identeco-vektoro)
        res-arhivo (reduce + 0.0 (map (fn [b] (* (sfero/resonanco (:v b) identeco-vektoro) (:pezo b)))
                                      arhivo))]
    
    (+ res-historica res-viva res-arhivo)))
;; --- VALIDACIÓN Y MINERÍA ---
(defn ĉu-nova-transakcio? [vektoro nonce]
  (let [h (sfero/haŝi-transakcion vektoro nonce)
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
