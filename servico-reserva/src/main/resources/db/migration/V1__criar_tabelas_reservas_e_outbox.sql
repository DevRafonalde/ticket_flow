CREATE TABLE reserva.reservas (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    evento_id UUID NOT NULL,
    usuario_id UUID NOT NULL,
    assentos INTEGER NOT NULL,
    situacao VARCHAR(50) NOT NULL DEFAULT 'PENDENTE',
    expira_em TIMESTAMP NOT NULL,
    criado_em TIMESTAMP NOT NULL DEFAULT now()
);

-- Padrão Outbox: eventos são gravados na mesma transação da reserva
-- e publicados no Kafka por um processo separado (relay).
CREATE TABLE reserva.eventos_saida (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_agregado UUID NOT NULL,
    tipo_evento VARCHAR(100) NOT NULL,
    dados JSONB NOT NULL,
    criado_em TIMESTAMP NOT NULL DEFAULT now(),
    publicado_em TIMESTAMP
);
