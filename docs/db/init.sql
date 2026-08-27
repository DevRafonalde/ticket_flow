-- Executado automaticamente pelo container do Postgres na primeira subida.
-- Cria um schema isolado por serviço dentro do banco único "ticketflow".
CREATE SCHEMA IF NOT EXISTS autenticacao AUTHORIZATION ticketflow;
CREATE SCHEMA IF NOT EXISTS catalogo AUTHORIZATION ticketflow;
CREATE SCHEMA IF NOT EXISTS reserva AUTHORIZATION ticketflow;
