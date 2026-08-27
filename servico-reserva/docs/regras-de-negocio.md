# Regras de negócio — servico-reserva

Parte de [`docs/regras-de-negocio.md`](../../docs/regras-de-negocio.md) (fonte da verdade completa). Este arquivo cobre apenas as regras do `servico-reserva`. Toda implementação (e todo teste) deste serviço deve refletir o que está descrito aqui. Se uma regra mudar, este arquivo muda junto no mesmo PR.

## 3. Reservas

### 3.1 Criação da reserva
- Cliente informa `idEvento` e quantidade de assentos desejada.
- Limite de **6 assentos por reserva** (medida simples anti-scalping/anti-bot).
- Ao criar a reserva:
  1. `assentosDisponiveis` do evento é decrementado **imediatamente** (hold) — não só na confirmação do pagamento. Isso evita overselling.
  2. A reserva nasce com situação `PENDENTE` e `expiraEm = now() + BOOKING_RESERVATION_TIMEOUT_MINUTES` (padrão 10 minutos).
- Se não houver assentos suficientes disponíveis no momento da criação, retorna `409 Conflict` (`EVENTO_ESGOTADO`) e nenhum assento é decrementado.

### 3.2 Controle de concorrência
- O decremento de `assentosDisponiveis` usa **lock otimista** (`@Version` na entidade do evento). Duas requisições simultâneas disputando o(s) último(s) assento(s) resultam em uma reserva bem-sucedida e outra recebendo `409 Conflict` (`RESERVA_CONFLITO`) — o cliente deve tratar esse código reconsultando a disponibilidade antes de tentar novamente.

### 3.3 Máquina de estados da reserva

```
PENDENTE --(pagamento aprovado)--> CONFIRMADA
PENDENTE --(timeout de 10 min sem pagamento)--> EXPIRADA
PENDENTE --(pagamento falhou, sem novas tentativas até o timeout)--> PENDENTE (permanece, pode tentar pagar de novo)
CONFIRMADA --(cliente cancela, até 24h antes do evento)--> CANCELADA
```

- Um job agendado roda a cada 1 minuto, busca reservas `PENDENTE` com `expiraEm` no passado, marca como `EXPIRADA` e devolve os assentos ao `assentosDisponiveis` do evento correspondente.
- Reserva `CONFIRMADA` só pode ser cancelada pelo próprio cliente até **24 horas antes** da data do evento. Depois disso, cancelamento não é permitido pelo endpoint (`403 Forbidden`, `RESERVA_JANELA_CANCELAMENTO_ENCERRADA`).
- Cancelamento de reserva `CONFIRMADA` devolve os assentos ao evento e publica o evento `ReservaCancelada`.

### 3.4 Pagamento (mock)
- `POST /reservas/{id}/pagar` simula um gateway de pagamento externo.
- Requer header `Idempotency-Key`: requisições repetidas com a mesma chave para a mesma reserva retornam a mesma resposta da primeira chamada, sem duplicar o processamento (evita cobrança dupla em caso de retry de rede).
- Só é possível pagar uma reserva `PENDENTE` e dentro da janela de `expiraEm`. Tentar pagar uma reserva `EXPIRADA`, `CONFIRMADA` ou `CANCELADA` retorna `409 Conflict` (`RESERVA_NAO_PAGAVEL`).
- Se o pagamento for aprovado: reserva vira `CONFIRMADA`, é publicado o evento `ReservaConfirmada` (padrão Outbox, ver 3.5).
- Se o pagamento falhar: reserva permanece `PENDENTE`, cliente pode tentar novamente até o `expiraEm`.

### 3.5 Padrão Outbox
- Toda mudança de estado relevante da reserva (confirmação, cancelamento, expiração) grava uma linha na tabela `reserva.eventos_saida` **na mesma transação** da mudança de estado.
- Um processo separado (relay) lê as linhas não publicadas (`publicado_em IS NULL`), publica no Kafka e marca como publicadas. Isso garante que o evento só é publicado se a transação de negócio foi de fato commitada — sem esse padrão, uma falha entre "salvar no banco" e "publicar no Kafka" deixaria os sistemas inconsistentes.

## Dados

- Schema Postgres: `reserva` (tabelas `reservas` e `eventos_saida`). Ver migração em `src/main/resources/db/migration/`.
