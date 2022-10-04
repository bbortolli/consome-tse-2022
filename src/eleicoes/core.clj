(ns eleicoes.core
  (:require
   [clojure.set :as clj-set]
   [clojure.string :as str]
   [clojure.pprint :as ppr]
   [jsonista.core :as json]
   [clj-http.client :as client]))

;; defs
(def api-url "https://resultados.tse.jus.br/oficial/ele2022/544/dados-simplificados/br/br-c0001-e000544-r.json")
(def headers {"Content-Type" "text/html; charset=utf-8"})
(def delay-retry-em-sec 25)

(def traducao-mapa
  {:nm :nome
   :n  :numero
   :cc :partido
   :vap :qtd-votos
   :pvap :percentual-votos})

(def chaves-usadas [:nome :numero :partido :qtd-votos :percentual-votos])

;; utils
(defn decode-keyword [s]
  (keyword (str/replace s \_ \-)))

(defn encode-keyword [kw]
  (str/replace (name kw) \- \_))

(def mapper-key-underscore
  (json/object-mapper
    {:decode-key-fn decode-keyword
     :encode-key-fn encode-keyword}))

(defn json->str [obj]
  (json/write-value-as-string obj mapper-key-underscore))

(defn str->json [s]
  (json/read-value s mapper-key-underscore))

(defn traduz-mapa [mapa]
  (clj-set/rename-keys mapa traducao-mapa))

(defn corrige-nome [candidato]
  (update candidato :nome #(-> (str/replace % #"É" "E")
                               (str/replace #"&apos;" "'"))))

(defn corrige-partido [candidato]
  (update candidato :partido #(-> (str/split % #" - ")
                                  first
                                  (str/replace "Ã" "A"))))

(defn corrige-candidato [candidato]
  (-> candidato
      corrige-nome
      corrige-partido))

(defn cls []
  (print (str (char 27) "[2J")))

;; main
(defn get-dados []
  (some-> (client/get api-url {:headers headers})
          :body
          str->json))

(defn ->dados-apresentacao [dados]
  (let [dados-candidatos (:cand dados)
        dados-traduzidos (map traduz-mapa dados-candidatos)
        dados-usados (map #(select-keys % chaves-usadas) dados-traduzidos)
        dados-corrigidos (map corrige-candidato dados-usados)]
    dados-corrigidos))

(defn hora-agora []
  (let [agora (java.util.Date.)
        formatter (java.text.SimpleDateFormat. "dd/MM/yyyy - HH:mm:ss")]
    (.format formatter agora)))

(defn apresenta-dados [data-hora dados-apresentacao]
  (cls)
  (ppr/print-table dados-apresentacao)
  (println (format "\n\nUltima Requisicao:  %s" (hora-agora)))
  (println (format "Ultima atualizacao: %s" data-hora)))

(defn rodar []
  (when-let [dados (get-dados)]
    (let [data-hora (format "%s - %s" (:dt dados) (:ht dados))
          dados-apresentacao (->dados-apresentacao dados)]
      (apresenta-dados data-hora dados-apresentacao))))

(defn rodar-sempre []
  (while true
    (rodar)
    (Thread/sleep (* delay-retry-em-sec 1000))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [auto? (some? (first args))]
    (if auto?
      (rodar-sempre)
      (rodar))))

(defn up [] (require 'eleicoes.core :reload-all))
