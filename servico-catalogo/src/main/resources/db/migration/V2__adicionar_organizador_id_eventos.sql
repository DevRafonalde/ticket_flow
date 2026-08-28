-- Dono do evento (id do usuário organizador, extraído do token JWT na criação).
-- Necessário para a regra "um ORGANIZADOR só edita/exclui os próprios eventos" (PUT/DELETE).
-- Nullable porque eventos já existentes antes desta coluna não têm organizador conhecido.
ALTER TABLE catalogo.eventos ADD COLUMN organizador_id UUID;