-- Executado automaticamente pelo container do Postgres na primeira subida.
-- Cria um schema isolado por serviço dentro do banco único "ticketflow".
CREATE SCHEMA IF NOT EXISTS auth AUTHORIZATION ticketflow;
CREATE SCHEMA IF NOT EXISTS catalog AUTHORIZATION ticketflow;
CREATE SCHEMA IF NOT EXISTS booking AUTHORIZATION ticketflow;
