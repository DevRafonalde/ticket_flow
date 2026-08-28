-- Flag de soft delete: DELETE /eventos/{id} marca ativo = false em vez de apagar a linha
-- (preserva o histórico para reservas já feitas). @SQLRestriction("ativo = true") na entidade
-- filtra automaticamente os eventos inativos de toda leitura.
ALTER TABLE catalogo.eventos ADD COLUMN ativo BOOL NOT NULL DEFAULT true;
