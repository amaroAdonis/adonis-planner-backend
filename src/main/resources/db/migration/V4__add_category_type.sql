-- Adiciona a coluna 'type' usada pela entidade Category
-- 1) cria a coluna como NULL para poder popular
ALTER TABLE categories
    ADD COLUMN type VARCHAR(20) NULL AFTER name;

-- 2) define um valor padrão para registros existentes
--    Ajuste o valor abaixo para o que seu enum usa (ex.: 'EXPENSE'/'INCOME')
UPDATE categories SET type = 'EXPENSE' WHERE type IS NULL;

-- 3) torna a coluna obrigatória
ALTER TABLE categories
    MODIFY COLUMN type VARCHAR(20) NOT NULL;
