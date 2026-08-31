# Regras de negócio — servico-autenticacao

Parte de [`docs/regras-de-negocio.md`](../../docs/regras-de-negocio.md) (fonte da verdade completa). Este arquivo cobre apenas as regras do `servico-autenticacao`. Toda implementação (e todo teste) deste serviço deve refletir o que está descrito aqui. Se uma regra mudar, este arquivo muda junto no mesmo PR.

## 1. Usuários e autenticação

### 1.1 Cadastro
- E-mail é único no sistema; tentativa de cadastro com e-mail já existente retorna `409 Conflict` (`AUTENTICACAO_EMAIL_JA_EXISTE`).
- Senha exige no mínimo 8 caracteres, contendo pelo menos 1 letra maiúscula e 1 número.
- Todo usuário é criado com o papel `CLIENTE` por padrão. Papéis `ORGANIZADOR` e `ADMIN` só podem ser atribuídos por um `ADMIN` já existente, através de um endpoint administrativo — **contrato ainda não definido em nenhum `docs/api.md`** (nem o path, nem o formato de request/response); definir antes de implementar.

### 1.2 Login e tokens
- Login bem-sucedido retorna um **token de acesso** (JWT, expira em `JWT_EXPIRATION_MINUTES`, padrão 60 min) e um **token de renovação** (expira em 7 dias).
- O token de acesso carrega `sub` (id do usuário), `papel` e `exp`. **Só os serviços de backend** (`servico-catalogo`, e futuramente `servico-reserva`) validam o token — conferem apenas a assinatura e a expiração, localmente, sem consultar o `servico-autenticacao` a cada requisição (stateless). O `servico-gateway` não participa dessa validação: é um roteador simples, sem lógica de negócio, que só encaminha o header `Authorization` adiante.
- O token de renovação é de uso único: ao ser trocado por um novo par de tokens, o antigo é invalidado (rotação de token de renovação).
- 3 tentativas de login inválidas consecutivas para o mesmo e-mail bloqueiam novas tentativas por 60 segundos (mitigação simples de força bruta).

### 1.3 OAuth2 (Google)
- Login social cria a conta automaticamente na primeira vez, vinculando o e-mail retornado pelo provedor. Não é definida senha local para contas criadas assim.
- Se o e-mail do provedor já existir como conta local (cadastro tradicional), a conta é vinculada — não são criadas contas duplicadas para o mesmo e-mail.

### 1.4 Autorização
- Endpoints de escrita no `servico-catalogo` (criar/editar/excluir evento) exigem papel `ORGANIZADOR` ou `ADMIN`.
- Um `ORGANIZADOR` só pode editar ou excluir os **próprios** eventos. Um `ADMIN` pode gerenciar eventos de qualquer organizador.
- Endpoints de reserva exigem qualquer usuário autenticado (`CLIENTE`, `ORGANIZADOR` ou `ADMIN` podem comprar ingressos).

## Dados

- Schema Postgres: `autenticacao` (tabela `usuarios`). Ver migração em `src/main/resources/db/migration/`.
