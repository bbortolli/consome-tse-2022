# Descricao

Projeto simples em Clojure para consumir a API do TSE que mostra os resultados das Eleições 2022.

# Como rodar?

1. Baixar o repositório
2. Ter o lein instalado localmente ou num container
3. Configurar o tempo do loop em segundos no código (delay-retry-em-sec)
4. Rodar lein run para executar uma única vez ou lein run <qualquer-coisa> para rodar em loop

## Docker

Na pasta raiz do projeto

### Cmder ou afins
```docker run -it --name clj -v %cd%:/work -w /work clojure:latest lein run```

### Powershell
```docker run -it --name clj -v ${PWD}/work -w /work clojure:latest lein run```
