(ns sfero.monujo
  (:require [sfero.core :as sfero]))

(defn krei-monujon [pasvorto]
  "Crea una billetera a partir de una contraseña o frase semilla."
  (let [seed (hash pasvorto)
        identeco (sfero/naski-vektoron seed)]
    {:seed seed
     :id-vektoro identeco
     :publika-adreso (sfero/permutado identeco 1337)})) ;; Dirección pública 'rotada'

(defn subskribi [transakcio-vektoro privata-id]
  "Firma una transacción vinculando (binding) el vector del mensaje 
   con la identidad del dueño."
  (sfero/ligi transakcio-vektoro privata-id))
