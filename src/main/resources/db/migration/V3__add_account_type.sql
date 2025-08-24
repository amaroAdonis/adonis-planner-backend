-- Adiciona a coluna 'type' usada pela entidade Account
-- 1) cria a coluna como NULL para poder popular
ALTER TABLE accounts
    ADD COLUMN type VARCHAR(20) NULL AFTER currency;

-- 2) define um valor padrão para registros existentes (ajuste se usar outro enum)
UPDATE accounts SET type = 'CHECKING' WHERE type IS NULL;

-- 3) torna a coluna obrigatória
ALTER TABLE accounts
    MODIFY COLUMN type VARCHAR(20) NOT NULL;