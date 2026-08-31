# Regras de negócio — servico-notificacao

Parte de [`docs/regras-de-negocio.md`](../../docs/regras-de-negocio.md) (fonte da verdade completa). Este arquivo cobre apenas as regras do `servico-notificacao`. Toda implementação (e todo teste) deste serviço deve refletir o que está descrito aqui. Se uma regra mudar, este arquivo muda junto no mesmo PR.

## 4. Notificações

- Consome os tópicos `reserva-confirmada` e `reserva-cancelada`.
- `ReservaConfirmada` dispara e-mail de confirmação com os detalhes da reserva.
- `ReservaCancelada` dispara e-mail informando o cancelamento da reserva. (Não há reembolso/estorno
  simulado a informar — `servico-reserva` não modela nenhum mecanismo de reembolso, só o pagamento
  mock na confirmação; se isso mudar, esta regra precisa ser revisitada junto.)
- **`reserva-expirada` não é consumido, de propósito.** O outbox do `servico-reserva` publica esse
  evento (ver `servico-reserva/docs/regras-de-negocio.md` 3.5) para outros fins (ex.: devolver os
  assentos ao catálogo, métricas), mas a decisão de produto é não notificar o cliente quando a
  própria reserva expira sem pagamento — não é uma lacuna a preencher depois.
- Falha no envio de e-mail **não** deve afetar o fluxo de reserva (o e-mail é uma consequência assíncrona, não uma dependência). Falhas são reprocessadas com backoff; após N tentativas, a mensagem vai para uma dead-letter topic para investigação manual.
