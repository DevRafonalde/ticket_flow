# TicketFlow

Plataforma de venda de ingressos para eventos, construída como um conjunto de microsserviços em Java + Spring Boot.

## Serviços

| Serviço | Porta | Responsabilidade |
|---|---|---|
| `gateway-service` | 8080 | Roteamento, rate limiting, ponto único de entrada |
| `auth-service` | 8081 | Cadastro, login, JWT, OAuth2 |
| `catalog-service` | 8082 | Eventos e disponibilidade (com cache Redis) |
| `booking-service` | 8083 | Reservas e pagamento |
| `notification-service` | 8084 | Consome eventos e envia notificações |

## Infraestrutura

- **PostgreSQL** único (`ticketflow`), com um **schema por serviço** (`auth`, `catalog`, `booking`)
- **Redis** para cache de disponibilidade no `catalog-service`
- **Kafka** (modo KRaft, sem Zookeeper) para comunicação assíncrona entre `booking-service` e `notification-service`

## Como rodar localmente

Pré-requisitos: Docker e Docker Compose instalados.

```bash
docker compose up --build
```

Isso sobe toda a infraestrutura (Postgres, Redis, Kafka) e os 5 serviços. Os schemas do Postgres são criados automaticamente pelo script em `docs/db/init.sql` na primeira subida do container.

## Documentação

- [`docs/architecture.md`](docs/architecture.md) — visão geral da arquitetura e decisões técnicas
