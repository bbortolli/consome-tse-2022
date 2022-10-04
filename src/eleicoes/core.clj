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
