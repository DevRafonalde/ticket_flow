# API — servico-autenticacao

Parte de [`docs/api.md`](../../docs/api.md) (guia completo da API, incluindo autenticação, formato de erro e rate limiting). Este arquivo cobre apenas os endpoints do `servico-autenticacao`, expostos pelo `servico-gateway` sob o prefixo `/api/autenticacao`.

## Autenticação

### `POST /api/autenticacao/register`

Cria uma nova conta de cliente.

**Request**
```json
{
  "nome": "Rafael Leão",
  "email": "rafael@example.com",
  "senha": "SenhaForte123"
}
```

**Response `201 Created`**
```json
{
  "id": "b3f1c2e0-...",
  "nome": "Rafael Leão",
  "email": "rafael@example.com",
  "papel": "CLIENTE"
}
```

**Erros possíveis**: `AUTENTICACAO_EMAIL_JA_EXISTE` (409), `VALIDACAO_ERRO` (400)

---

### `POST /api/autenticacao/login`

**Request**
```json
{
  "email": "rafael@example.com",
  "senha": "SenhaForte123"
}
```

**Response `200 OK`**
```json
{
  "tokenAcesso": "eyJhbGciOi...",
  "tokenRenovacao": "8f3e2a1b-...",
  "expiraEmSegundos": 3600
}
```

**Erros possíveis**: `AUTENTICACAO_CREDENCIAIS_INVALIDAS` (401), `AUTENTICACAO_MUITAS_TENTATIVAS` (429)

---

### `POST /api/autenticacao/refresh`

Troca um token de renovação válido por um novo par de tokens. O token de renovação usado é invalidado (uso único).

**Request**
```json
{ "tokenRenovacao": "8f3e2a1b-..." }
```

**Response `200 OK`**: mesmo formato do login.

**Erros possíveis**: `AUTENTICACAO_TOKEN_RENOVACAO_INVALIDO` (401)

---

### `GET /api/autenticacao/oauth2/google`

Redireciona para o fluxo de login do Google. Ao final, redireciona de volta para o frontend com `tokenAcesso` e `tokenRenovacao` como query params na URL de callback configurada.
