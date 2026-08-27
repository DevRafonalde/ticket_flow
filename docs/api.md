# API TicketFlow — Guia para o frontend

A documentação da API foi dividida por serviço, junto ao código que a implementa. Comece por [`servico-gateway/docs/api.md`](../servico-gateway/docs/api.md) — cobre a base URL, autenticação, formato padrão de erro, rate limiting e o fluxo recomendado de compra — e depois consulte o serviço específico para o detalhe de cada endpoint:

| Prefixo | Serviço | Documentação |
|---|---|---|
| Visão geral, erros, rate limiting | `servico-gateway` | [`servico-gateway/docs/api.md`](../servico-gateway/docs/api.md) |
| `/api/autenticacao/**` | `servico-autenticacao` | [`servico-autenticacao/docs/api.md`](../servico-autenticacao/docs/api.md) |
| `/api/catalogo/**` | `servico-catalogo` | [`servico-catalogo/docs/api.md`](../servico-catalogo/docs/api.md) |
| `/api/reservas/**` | `servico-reserva` | [`servico-reserva/docs/api.md`](../servico-reserva/docs/api.md) |

Ver também [`docs/regras-de-negocio.md`](regras-de-negocio.md) para as regras de negócio por trás de cada endpoint.
