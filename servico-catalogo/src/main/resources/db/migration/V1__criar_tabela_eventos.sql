CREATE TABLE catalogo.eventos (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(255) NOT NULL,
    local VARCHAR(255) NOT NULL,
    data_evento TIMESTAMP NOT NULL,
    total_assentos INTEGER NOT NULL,
    assentos_disponiveis INTEGER NOT NULL,
    criado_em TIMESTAMP NOT NULL DEFAULT now(),
    atualizado_em TIMESTAMP NOT NULL DEFAULT now()
);
