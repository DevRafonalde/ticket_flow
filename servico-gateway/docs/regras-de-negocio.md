# Regras de negócio — servico-gateway

Parte de [`docs/regras-de-negocio.md`](../../docs/regras-de-negocio.md) (fonte da verdade completa). Este arquivo cobre as regras transversais, aplicadas pelo `servico-gateway` ou por todos os serviços de backend.

## 5. Regras transversais

- Toda escrita relevante (criação/alteração de evento, criação/confirmação/cancelamento de reserva) gera um registro de auditoria: quem fez, quando, e o payload da mudança. **Em aberto**: nenhum schema/tabela foi definido para guardar esses registros, e nenhum serviço tem código relacionado ainda — decidir o dono (schema próprio? tabela por serviço? serviço dedicado?) antes de implementar.
- O `servico-gateway` aplica rate limiting por IP (padrão sugerido: 60 requisições/minuto) para mitigar bots tentando esgotar o estoque de ingressos.
- Todos os erros de negócio seguem o formato padronizado descrito em [`docs/api.md`](../../docs/api.md). **Estado atual**: só `servico-catalogo` já está nesse formato (campo `codigo` por exceção, `ErroResposta` com `dataHora`/`codigoStatus`/...); os outros 4 serviços ainda usam a versão anterior do scaffold de exceções — ver `docs/architecture.md` §5.
