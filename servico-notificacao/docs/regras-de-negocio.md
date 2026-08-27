# Regras de negócio — servico-notificacao

Parte de [`docs/regras-de-negocio.md`](../../docs/regras-de-negocio.md) (fonte da verdade completa). Este arquivo cobre apenas as regras do `servico-notificacao`. Toda implementação (e todo teste) deste serviço deve refletir o que está descrito aqui. Se uma regra mudar, este arquivo muda junto no mesmo PR.

## 4. Notificações

- Consome os tópicos `reserva-confirmada` e `reserva-cancelada`.
- `ReservaConfirmada` dispara e-mail de confirmação com os detalhes da reserva.
- `ReservaCancelada` dispara e-mail informando o cancelamento e o reembolso (simulado).
- Falha no envio de e-mail **não** deve afetar o fluxo de reserva (o e-mail é uma consequência assíncrona, não uma dependência). Falhas são reprocessadas com backoff; após N tentativas, a mensagem vai para uma dead-letter topic para investigação manual.
