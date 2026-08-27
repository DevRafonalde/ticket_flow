# Arquitetura — TicketFlow

## Visão geral

O sistema é dividido em 5 serviços independentes que se comunicam via REST (síncrono) e Kafka (assíncrono). Ver diagramas em `docs/diagrams/`.

## Decisões técnicas

- **Um Postgres, schema por serviço**: optamos por um único banco (`ticketflow`) com schemas separados (`auth`, `catalog`, `booking`) em vez de uma instância por serviço. Mantém isolamento lógico dos dados sem a sobrecarga operacional de múltiplos bancos — trade-off razoável para um projeto deste porte. Evolução natural: separar em bancos físicos por serviço.
- **Kafka em modo KRaft**: dispensa Zookeeper, reduzindo a complexidade da infraestrutura local.
- **Outbox pattern no booking-service**: garante que o evento de reserva só é publicado se a transação de negócio foi commitada com sucesso.
- **Redis com TTL curto no catalog-service**: cache de disponibilidade, invalidado por evento quando uma reserva é confirmada.

## Próximos passos

Ver roadmap em fases no plano técnico do projeto.
