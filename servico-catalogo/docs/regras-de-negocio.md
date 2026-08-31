# Regras de negócio — servico-catalogo

Parte de [`docs/regras-de-negocio.md`](../../docs/regras-de-negocio.md) (fonte da verdade completa). Este arquivo cobre apenas as regras do `servico-catalogo`. Toda implementação (e todo teste) deste serviço deve refletir o que está descrito aqui. Se uma regra mudar, este arquivo muda junto no mesmo PR.

## 2. Catálogo de eventos

### 2.1 Criação e edição
- Um evento tem: nome, local, data/hora, `totalAssentos`, `assentosDisponiveis` e o id do
  organizador dono (`organizadorId`) — interno, não exposto na API pública, usado só para a
  checagem de posse em 1.4.
- `assentosDisponiveis` nunca pode ser maior que `totalAssentos` (invariante validado em toda escrita).
- Não é permitido criar **ou editar** um evento com `dataEvento` no passado — a mesma validação
  vale para os dois, já que criação e edição usam o mesmo corpo de requisição. Na prática isso
  também impede editar qualquer outro campo (nome, local, `totalAssentos`) de um evento cuja data
  já passou, mesmo reenviando a mesma `dataEvento`; se esse efeito colateral não for desejado, é um
  ponto a revisitar quando o fluxo de edição for repensado (ex.: DTOs separados para criação e
  edição, ou não revalidar `dataEvento` quando ela não mudou).
- `totalAssentos` não pode ser reduzido para um valor menor que `totalAssentos - assentosDisponiveis` (ou seja, menor que a quantidade já reservada/vendida).

### 2.2 Exclusão
- Um evento não pode ser excluído se possuir reservas com situação `PENDENTE` ou `CONFIRMADA`. Retorna `409 Conflict` (`EVENTO_POSSUI_RESERVAS_ATIVAS`).
- Eventos com apenas reservas `EXPIRADA` ou `CANCELADA` podem ser excluídos normalmente.
- Essa regra é aplicada de forma indireta: o catálogo não conhece a situação da reserva, só o
  quanto falta de `assentosDisponiveis` em relação a `totalAssentos`
  (`assentosDisponiveis < totalAssentos` ⇒ existe reserva ativa). Isso só fica correto se o
  `servico-reserva` sempre devolver os assentos via `PATCH /eventos/{id}/liberar` (ver
  `docs/api.md`, ainda não implementado) ao expirar ou cancelar uma reserva — sem essa chamada, o
  evento fica permanentemente "bloqueado" para exclusão mesmo depois que a reserva deixou de estar
  ativa.

### 2.3 Disponibilidade e cache
- A consulta de disponibilidade (`GET /eventos/{id}/disponibilidade`) é cacheada no Redis com TTL de 30 segundos (cache-aside).
- **Desenho alvo**: o cache é invalidado **antes** do TTL expirar sempre que o `servico-reserva`
  publica um evento de reserva criada, confirmada, cancelada ou expirada — o `servico-catalogo`
  consome esse evento e limpa a chave correspondente. Isso evita que o frontend mostre assentos
  como disponíveis quando já foram reservados por outro cliente.
- **Implementado hoje**: o mecanismo existente (`CacheEvictionListenerConfig`) não é o acima — é
  um canal genérico de Redis Pub/Sub (`servico-catalogo:cache-evict`) que, ao receber qualquer
  mensagem, limpa **todos** os caches, não uma chave específica. Nada publica nesse canal ainda
  (`servico-reserva` não existe). Até lá, a disponibilidade cacheada só se corrige pelo TTL de
  30s. Ver `docs/architecture.md` §7 para o detalhe da divergência.

## Dados

- Schema Postgres: `catalogo` (tabela `eventos`, colunas `total_assentos` e `assentos_disponiveis`). Ver migração em `src/main/resources/db/migration/`.
