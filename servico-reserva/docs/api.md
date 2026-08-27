# API — servico-reserva

Parte de [`docs/api.md`](../../docs/api.md) (guia completo da API, incluindo autenticação, formato de erro e rate limiting). Este arquivo cobre apenas os endpoints do `servico-reserva`, expostos pelo `servico-gateway` sob o prefixo `/api/reservas`.

## Reservas

### `POST /api/reservas`

Cria uma reserva (hold de assentos). Requer autenticação.

**Request**
```json
{
  "idEvento": "a1b2c3d4-...",
  "assentos": 2
}
```

**Response `201 Created`**
```json
{
  "id": "f9e8d7c6-...",
  "idEvento": "a1b2c3d4-...",
  "assentos": 2,
  "situacao": "PENDENTE",
  "expiraEm": "2026-10-01T14:32:00Z"
}
```

> Dica de frontend: use `expiraEm` para exibir um contador regressivo na tela de checkout — a reserva é liberada automaticamente se o pagamento não for concluído até esse horário.

**Erros possíveis**: `EVENTO_ESGOTADO` (409), `RESERVA_CONFLITO` (409, tentar novamente após reconsultar disponibilidade), `VALIDACAO_ERRO` (400, ex: mais de 6 assentos)

---

### `GET /api/reservas/{id}`

Detalhe de uma reserva. Só o próprio dono da reserva (ou `ADMIN`) pode consultar.

**Response `200 OK`**: mesmo formato da criação, incluindo a `situacao` atualizada (`PENDENTE`, `CONFIRMADA`, `EXPIRADA`, `CANCELADA`).

---

### `GET /api/reservas`

Lista as reservas do usuário autenticado (paginado, mesmo formato de paginação da listagem de eventos).

---

### `POST /api/reservas/{id}/pagar`

Confirma o pagamento (mock) de uma reserva `PENDENTE`.

**Headers obrigatórios**
```
Idempotency-Key: <uuid gerado pelo frontend por tentativa de pagamento>
```

**Response `200 OK`**
```json
{
  "id": "f9e8d7c6-...",
  "situacao": "CONFIRMADA"
}
```

**Erros possíveis**: `RESERVA_NAO_PAGAVEL` (409 — reserva expirada, já paga ou cancelada), `PAGAMENTO_FALHOU` (402)

---

### `DELETE /api/reservas/{id}`

Cancela uma reserva `CONFIRMADA`. Só permitido até 24h antes do evento.

**Response**: `204 No Content`

**Erros possíveis**: `RESERVA_JANELA_CANCELAMENTO_ENCERRADA` (403)
