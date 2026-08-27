# TicketFlow

Plataforma de venda de ingressos para eventos, construída como um conjunto de microsserviços em Java + Spring Boot.

## Serviços

| Serviço                | Porta | Responsabilidade                                  |
|------------------------|-------|---------------------------------------------------|
| `servico-gateway`      | 8080  | Roteamento, rate limiting, ponto único de entrada |
| `servico-autenticacao` | 8081  | Cadastro, login, JWT, OAuth2                      |
| `servico-catalogo`     | 8082  | Eventos e disponibilidade (com cache Redis)       |
| `servico-reserva`      | 8083  | Reservas e pagamento                              |
| `servico-notificacao`  | 8084  | Consome eventos e envia notificações              |

## Infraestrutura

- **PostgreSQL** único (`ticketflow`), com um **schema por serviço** (`autenticacao`, `catalogo`, `reserva`)
- **Redis** para cache de disponibilidade no `servico-catalogo`
- **Kafka** (modo KRaft, sem Zookeeper) para comunicação assíncrona entre `servico-reserva` e `servico-notificacao`

## Como rodar localmente

Pré-requisitos: Docker e Docker Compose instalados.

```bash
docker compose up --build
```

Isso sobe toda a infraestrutura (Postgres, Redis, Kafka) e os 5 serviços. Os schemas do Postgres são criados automaticamente pelo script em `docs/db/init.sql` na primeira subida do container.

## Documentação

- [`docs/architecture.md`](docs/architecture.md) — visão geral da arquitetura e decisões técnicas
- [`docs/regras-de-negocio.md`](docs/regras-de-negocio.md) — regras de negócio, divididas por serviço
- [`docs/api.md`](docs/api.md) — guia de API para o frontend, dividido por serviço
