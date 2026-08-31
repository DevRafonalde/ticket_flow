# API — servico-catalogo

Parte de [`docs/api.md`](../../docs/api.md) (guia completo da API, incluindo autenticação, formato de erro e rate limiting). Este arquivo cobre apenas os endpoints do `servico-catalogo`, expostos pelo `servico-gateway` sob o prefixo `/api/catalogo`.

## Catálogo de eventos

### `GET /api/catalogo/eventos`

Lista eventos com paginação e filtros. **Não exige autenticação.**

**Query params**

| Param | Tipo | Obrigatório | Descrição |
|---|---|---|---|
| `pagina` | int | não (padrão 0) | Página, começando em 0 |
| `tamanho` | int | não (padrão 20) | Itens por página |
| `nome` | string | não | Busca parcial pelo nome do evento |
| `de` | date | não | Data inicial do filtro |
| `ate` | date | não | Data final do filtro |

**Response `200 OK`**
```json
{
  "conteudo": [
    {
      "id": "a1b2c3d4-...",
      "nome": "Show da Banda X",
      "local": "Arena Y",
      "dataEvento": "2026-11-20T22:00:00",
      "totalAssentos": 5000,
      "assentosDisponiveis": 1200
    }
  ],
  "pagina": 0,
  "tamanho": 20,
  "totalElementos": 134,
  "totalPaginas": 7
}
```

---

### `GET /api/catalogo/eventos/{id}`

Detalhe de um evento. Não exige autenticação.

**Response `200 OK`**: mesmo objeto do item da listagem acima.

**Erros possíveis**: `EVENTO_NAO_ENCONTRADO` (404)

---

### `GET /api/catalogo/eventos/{id}/disponibilidade`

Consulta rápida de disponibilidade (endpoint cacheado, ideal para polling no frontend antes de abrir o fluxo de reserva).

**Response `200 OK`**
```json
{ "idEvento": "a1b2c3d4-...", "assentosDisponiveis": 1200 }
```

> Dica de frontend: este endpoint tem cache de até 30s no backend. Para uma tela de checkout onde a precisão importa mais que a performance, prefira confiar na resposta de `POST /api/reservas` (que sempre lê o dado mais atual) em vez de fazer polling agressivo aqui.

---

### `POST /api/catalogo/eventos` — requer role `ORGANIZADOR` ou `ADMIN`

**Request**
```json
{
  "nome": "Show da Banda X",
  "local": "Arena Y",
  "dataEvento": "2026-11-20T22:00:00",
  "totalAssentos": 5000
}
```

**Response `201 Created`**: objeto do evento criado.

**Erros possíveis**: `VALIDACAO_ERRO` (400, ex: data no passado), `AUTENTICACAO_ACESSO_NEGADO` (403)

---

### `PUT /api/catalogo/eventos/{id}` — requer ser o organizador dono do evento, ou `ADMIN`

Mesmo corpo do `POST`. Retorna `403 Forbidden` (`AUTENTICACAO_ACESSO_NEGADO`) se o organizador não for o dono do evento.

---

### `DELETE /api/catalogo/eventos/{id}` — requer ser o organizador dono do evento, ou `ADMIN`

**Response**: `204 No Content`

**Erros possíveis**: `EVENTO_POSSUI_RESERVAS_ATIVAS` (409) — evento com reservas ativas não pode ser excluído.

---

## Uso interno (servico-a-servico)

Não fazem parte da API pública exposta ao frontend pelo `servico-gateway` - chamados diretamente
entre serviços (ex: `http://servico-catalogo:8082`), autenticados com um segredo compartilhado
diferente do JWT de usuário (header `X-Internal-Api-Key`, valor em `INTERNAL_API_KEY`), já que
quem chama é outro backend, não um usuário logado.

### `PATCH /api/catalogo/eventos/{id}/reservar`

Usado pelo `servico-reserva` ao criar uma reserva, para debitar os assentos do estoque.

**Request**
```json
{ "quantidade": 2 }
```

**Response**: `204 No Content`

O desconto é atômico (um único `UPDATE` condicional no banco) - sob concorrência, duas reservas
não conseguem debitar o mesmo último assento.

**Erros possíveis**: `VALIDACAO_ERRO` (400), `AUTENTICACAO_INTERNA_INVALIDA` (401) — chave interna
ausente/errada, `EVENTO_NAO_ENCONTRADO` (404), `EVENTO_ESGOTADO` (409) — sem assentos suficientes
para a quantidade pedida.

---

### `PATCH /api/catalogo/eventos/{id}/liberar`

> **Ainda não implementado.** Contrato documentado para o `servico-reserva` integrar contra ele
> assim que existir — ver `docs/architecture.md` §9. Operação inversa de `/reservar`.

Usado pelo `servico-reserva` para devolver assentos ao estoque de um evento: quando uma reserva
`PENDENTE` expira sem pagamento (job de expiração), ou quando uma reserva `CONFIRMADA` é
cancelada pelo cliente.

**Request**
```json
{ "quantidade": 2 }
```

**Response**: `204 No Content`

O incremento deve ser atômico e limitado a `totalAssentos` — assim como `/reservar`, não pode
deixar `assentosDisponiveis` ultrapassar `totalAssentos`, mesmo sob chamadas concorrentes ou
duplicadas (ex.: retry de rede do relay do outbox).

**Erros possíveis**: `VALIDACAO_ERRO` (400), `AUTENTICACAO_INTERNA_INVALIDA` (401) — chave interna
ausente/errada, `EVENTO_NAO_ENCONTRADO` (404).
