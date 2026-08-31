# API TicketFlow — Guia para o frontend (servico-gateway)

Todas as chamadas do frontend passam pelo **`servico-gateway`**, que é o único ponto de entrada público. Nunca chame os serviços internos diretamente.

- **Base URL (local)**: `http://localhost:8080/api`
- **Formato**: JSON em todas as requisições e respostas
- **Datas**: ISO-8601 sem timezone explícita, ex: `2026-11-20T22:00:00` — reflete o uso de `LocalDateTime` na implementação atual (`servico-catalogo`); tratar como horário local do servidor, não UTC. Corrigir esta seção se/quando os serviços migrarem para `Instant`/`OffsetDateTime`.
- **Autenticação**: header `Authorization: Bearer <token_de_acesso>` em todos os endpoints exceto cadastro, login e listagem pública de eventos

Endpoints de cada domínio estão documentados junto ao serviço dono:

| Prefixo | Serviço | Documentação |
|---|---|---|
| `/api/autenticacao/**` | `servico-autenticacao` | [`servico-autenticacao/docs/api.md`](../../servico-autenticacao/docs/api.md) |
| `/api/catalogo/**` | `servico-catalogo` | [`servico-catalogo/docs/api.md`](../../servico-catalogo/docs/api.md) |
| `/api/reservas/**` | `servico-reserva` | [`servico-reserva/docs/api.md`](../../servico-reserva/docs/api.md) |

---

## Formato padrão de erro

Todas as respostas de erro seguem este formato:

```json
{
  "dataHora": "2026-08-27T20:15:00",
  "codigoStatus": 409,
  "erro": "EVENTO_ESGOTADO",
  "mensagem": "Não há assentos suficientes disponíveis para este evento.",
  "caminho": "/api/reservas"
}
```

Erros de validação (`400`) incluem um campo adicional `detalhes` com a lista de campos inválidos:

```json
{
  "dataHora": "2026-08-27T20:15:00",
  "codigoStatus": 400,
  "erro": "VALIDACAO_ERRO",
  "mensagem": "Um ou mais campos são inválidos.",
  "caminho": "/api/reservas",
  "detalhes": [
    { "campo": "assentos", "mensagem": "deve ser entre 1 e 6" }
  ]
}
```

### Tabela de códigos de erro

| Código | HTTP | Onde ocorre |
|---|---|---|
| `VALIDACAO_ERRO` | 400 | Qualquer endpoint com corpo inválido |
| `AUTENTICACAO_CREDENCIAIS_INVALIDAS` | 401 | Login |
| `AUTENTICACAO_TOKEN_EXPIRADO` | 401 | Qualquer endpoint autenticado |
| `AUTENTICACAO_TOKEN_RENOVACAO_INVALIDO` | 401 | Refresh |
| `AUTENTICACAO_MUITAS_TENTATIVAS` | 429 | Login |
| `AUTENTICACAO_ACESSO_NEGADO` | 403 | Ações fora da role/posse do recurso |
| `AUTENTICACAO_EMAIL_JA_EXISTE` | 409 | Cadastro |
| `AUTENTICACAO_INTERNA_INVALIDA` | 401 | Chamada servico-a-serviço com chave interna ausente/errada — uso interno, não trafega pelo gateway (ver seção "Uso interno" de cada serviço) |
| `EVENTO_NAO_ENCONTRADO` | 404 | Catálogo |
| `EVENTO_ESGOTADO` | 409 | Criação de reserva — sem estoque suficiente, seja por falta de assentos ou por perder a corrida de concorrência para outra reserva simultânea |
| `EVENTO_POSSUI_RESERVAS_ATIVAS` | 409 | Exclusão de evento |
| `RESERVA_NAO_PAGAVEL` | 409 | Pagamento de reserva |
| `RESERVA_JANELA_CANCELAMENTO_ENCERRADA` | 403 | Cancelamento de reserva |
| `PAGAMENTO_FALHOU` | 402 | Pagamento de reserva |

---

## Rate limiting

O `servico-gateway` retorna os headers `X-RateLimit-Limit` e `X-RateLimit-Remaining` em toda resposta. Ao exceder o limite, retorna `429 Too Many Requests` com um header `Retry-After` (em segundos).

---

## Fluxo recomendado de compra (frontend)

1. `GET /api/catalogo/eventos` — listar eventos, com filtros de busca.
2. `GET /api/catalogo/eventos/{id}` — tela de detalhe do evento.
3. `POST /api/reservas` — ao clicar em "comprar", cria a reserva e recebe `expiraEm`.
4. Exibir contador regressivo baseado em `expiraEm`.
5. `POST /api/reservas/{id}/pagar` com `Idempotency-Key` — ao confirmar o pagamento.
   - Se `RESERVA_NAO_PAGAVEL`, a reserva expirou: informar o usuário e voltar ao passo 3.
   - Se `PAGAMENTO_FALHOU`, permitir nova tentativa enquanto `expiraEm` não passou.
6. Em caso de `409 EVENTO_ESGOTADO` no passo 3 (seja por falta de estoque ou por perder a corrida de concorrência para outra reserva simultânea), reconsultar `GET /api/catalogo/eventos/{id}/disponibilidade` antes de deixar o usuário tentar de novo.
