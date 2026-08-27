# Regras de negócio — servico-catalogo

Parte de [`docs/regras-de-negocio.md`](../../docs/regras-de-negocio.md) (fonte da verdade completa). Este arquivo cobre apenas as regras do `servico-catalogo`. Toda implementação (e todo teste) deste serviço deve refletir o que está descrito aqui. Se uma regra mudar, este arquivo muda junto no mesmo PR.

## 2. Catálogo de eventos

### 2.1 Criação e edição
- Um evento tem: nome, local, data/hora, `totalAssentos` e `assentosDisponiveis`.
- `assentosDisponiveis` nunca pode ser maior que `totalAssentos` (invariante validado em toda escrita).
- Não é permitido criar evento com data no passado.
- `totalAssentos` não pode ser reduzido para um valor menor que `totalAssentos - assentosDisponiveis` (ou seja, menor que a quantidade já reservada/vendida).

### 2.2 Exclusão
- Um evento não pode ser excluído se possuir reservas com situação `PENDENTE` ou `CONFIRMADA`. Retorna `409 Conflict` (`EVENTO_POSSUI_RESERVAS_ATIVAS`).
- Eventos com apenas reservas `EXPIRADA` ou `CANCELADA` podem ser excluídos normalmente.

### 2.3 Disponibilidade e cache
- A consulta de disponibilidade (`GET /eventos/{id}/disponibilidade`) é cacheada no Redis com TTL de 30 segundos (cache-aside).
- O cache é invalidado **antes** do TTL expirar sempre que o `servico-reserva` publica um evento de reserva criada, confirmada, cancelada ou expirada — o `servico-catalogo` consome esse evento e limpa a chave correspondente. Isso evita que o frontend mostre assentos como disponíveis quando já foram reservados por outro cliente.

## Dados

- Schema Postgres: `catalogo` (tabela `eventos`, colunas `total_assentos` e `assentos_disponiveis`). Ver migração em `src/main/resources/db/migration/`.
