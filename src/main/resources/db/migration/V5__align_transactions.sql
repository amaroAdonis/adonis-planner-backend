-- V5__align_transactions.sql
-- Alinha a tabela de transações com a camada de domínio/serviço
-- - Garante tipos e nulabilidade esperados
-- - Ajusta FK de category para aceitar NULL
-- - Cria índices úteis para consultas por usuário/período e conta/período

-- 1) Garante tipos/nulabilidade das colunas principais
ALTER TABLE transactions
    MODIFY COLUMN `date` DATE NOT NULL,
    MODIFY COLUMN `type` VARCHAR(20) NOT NULL,
    MODIFY COLUMN `amount` DECIMAL(19,2) NOT NULL,
    MODIFY COLUMN `description` VARCHAR(255) NULL,
    MODIFY COLUMN `category_id` BIGINT NULL,
    MODIFY COLUMN `account_id` BIGINT NOT NULL,
    MODIFY COLUMN `user_id` BIGINT NOT NULL;

-- 2) Ajusta FKs (idempotente: droppa se existir e recria com regras corretas)

-- 2.1) FK -> users
ALTER TABLE transactions
    DROP FOREIGN KEY IF EXISTS fk_tx_user;
ALTER TABLE transactions
    ADD CONSTRAINT fk_tx_user
        FOREIGN KEY (user_id) REFERENCES users(id)
            ON DELETE RESTRICT ON UPDATE CASCADE;

-- 2.2) FK -> accounts
ALTER TABLE transactions
    DROP FOREIGN KEY IF EXISTS fk_tx_account;
ALTER TABLE transactions
    ADD CONSTRAINT fk_tx_account
        FOREIGN KEY (account_id) REFERENCES accounts(id)
            ON DELETE RESTRICT ON UPDATE CASCADE;

-- 2.3) FK -> categories (pode ser NULL; se apagar categoria, mantém transação com category_id NULL)
ALTER TABLE transactions
    DROP FOREIGN KEY IF EXISTS fk_tx_category;
ALTER TABLE transactions
    ADD CONSTRAINT fk_tx_category
        FOREIGN KEY (category_id) REFERENCES categories(id)
            ON DELETE SET NULL ON UPDATE CASCADE;

-- 3) Índices para performance em listagens por período
-- (remonta os índices se já existirem)
DROP INDEX IF EXISTS idx_tx_user_date ON transactions;
CREATE INDEX idx_tx_user_date ON transactions(user_id, `date`);

DROP INDEX IF EXISTS idx_tx_account_date ON transactions;
CREATE INDEX idx_tx_account_date ON transactions(account_id, `date`);

-- 4) (Opcional) índice por categoria para filtros futuros
DROP INDEX IF EXISTS idx_tx_category_date ON transactions;
CREATE INDEX idx_tx_category_date ON transactions(category_id, `date`);
