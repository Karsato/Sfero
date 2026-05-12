# 🌌 Sfero

**Sfero** es un libro mayor distribuido (*distributed ledger*) experimental que utiliza **Arquitectura Simbólica Vectorial (VSA)** para gestionar identidades, saldos y transacciones. En lugar de bases de datos relacionales estándar, el estado de la red se codifica en hipervectores de alta dimensión, donde la validez de las operaciones se determina por resonancia geométrica.

## 🚀 Instrucciones de Uso (Docker)

El proyecto está diseñado para ejecutarse en un entorno contenedorizado, simulando un enjambre de nodos interconectados.

### 1. Levantar el enjambre
Desde la raíz del proyecto, ejecuta el siguiente comando para iniciar los nodos (por defecto: `node1`, `node2` y `node3`):

```bash
docker-compose u
p -d
