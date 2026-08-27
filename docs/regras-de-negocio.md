# Regras de negócio — TicketFlow

As regras de negócio foram divididas por serviço, junto ao código que as implementa. Cada arquivo abaixo é a fonte da verdade para o respectivo serviço — toda implementação (e todo teste) deve refletir o que está descrito nele. Se uma regra mudar, o arquivo do serviço muda junto no mesmo PR.

| Seção | Serviço | Documentação |
|---|---|---|
| 1. Usuários e autenticação | `servico-autenticacao` | [`servico-autenticacao/docs/regras-de-negocio.md`](../servico-autenticacao/docs/regras-de-negocio.md) |
| 2. Catálogo de eventos | `servico-catalogo` | [`servico-catalogo/docs/regras-de-negocio.md`](../servico-catalogo/docs/regras-de-negocio.md) |
| 3. Reservas | `servico-reserva` | [`servico-reserva/docs/regras-de-negocio.md`](../servico-reserva/docs/regras-de-negocio.md) |
| 4. Notificações | `servico-notificacao` | [`servico-notificacao/docs/regras-de-negocio.md`](../servico-notificacao/docs/regras-de-negocio.md) |
| 5. Regras transversais | `servico-gateway` | [`servico-gateway/docs/regras-de-negocio.md`](../servico-gateway/docs/regras-de-negocio.md) |

Ver também [`docs/api.md`](api.md) para o formato de erro padronizado usado por todos os serviços.
