FROM debian:bookworm-slim

# 1. Instalamos las herramientas base del sistema
# Añadimos 'iproute2' y 'iputils-ping' para poder testear la red entre nodos
RUN apt-get update && apt-get install -y \
    openjdk-17-jdk-headless \
    curl \
    git \
    bash \
    rlwrap \
    iproute2 \
    iputils-ping \
    && rm -rf /var/lib/apt/lists/*

# 2. Instalamos la CLI oficial desde el repo brew-install
# Cambiamos a la URL de clojure/brew-install que es la recomendada ahora
RUN curl -L https://github.com/clojure/brew-install/releases/latest/download/linux-install.sh -o install_clojure.sh \
    && chmod +x install_clojure.sh \
    && ./install_clojure.sh \
    && rm install_clojure.sh

ENV LANG C.UTF-8

WORKDIR /app

# 3. Pre-descarga de dependencias (para que el arranque sea instantáneo)
COPY deps.edn .
RUN clj -P

# 4. Copiamos el resto de tu código Sfero
COPY . .

EXPOSE 8080

# Usamos ENTRYPOINT para que los argumentos del docker-compose lleguen al nodo
ENTRYPOINT ["clojure", "-M:genesis"]
