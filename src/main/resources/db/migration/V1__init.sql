-- =======================================
-- Planner Financeiro - Banco de Dados
-- MySQL 8 / Community Edition
-- =======================================

-- Criar o schema (banco)
CREATE DATABASE IF NOT EXISTS planner
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;

USE planner;

-- ============================
-- Tabela: users
-- ============================
CREATE TABLE IF NOT EXISTS users (
                                     id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                     name VARCHAR(120) NOT NULL,
    email VARCHAR(160) NOT NULL UNIQUE,
    password_hash VARCHAR(200) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP
    ) ENGINE=InnoDB;

-- ============================
-- Tabela: accounts
-- ============================
CREATE TABLE IF NOT EXISTS accounts (
                                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                        user_id BIGINT NOT NULL,
                                        name VARCHAR(120) NOT NULL,
    account_type ENUM('CASH','CHECKING','SAVINGS','CREDIT','INVESTMENT') NOT NULL,
    currency CHAR(3) NOT NULL DEFAULT 'BRL',
    opening_balance DECIMAL(14,2) NOT NULL DEFAULT 0.00,
    is_archived TINYINT(1) NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_accounts_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT uq_accounts_user_name UNIQUE (user_id, name)
    ) ENGINE=InnoDB;

-- ============================
-- Tabela: categories
-- ============================
CREATE TABLE IF NOT EXISTS categories (
                                          id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                          user_id BIGINT NOT NULL,
                                          parent_id BIGINT NULL,
                                          name VARCHAR(120) NOT NULL,
    category_type ENUM('INCOME','EXPENSE','TRANSFER') NOT NULL,
    color VARCHAR(16) NULL,
    icon VARCHAR(64) NULL,
    is_archived TINYINT(1) NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_categories_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_categories_parent FOREIGN KEY (parent_id) REFERENCES categories(id) ON DELETE SET NULL,
    CONSTRAINT uq_categories_user_name_type_parent UNIQUE (user_id, name, category_type, parent_id)
    ) ENGINE=InnoDB;

-- ============================
-- Tabela: transactions
-- ============================
CREATE TABLE IF NOT EXISTS transactions (
                                            id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                            user_id BIGINT NOT NULL,
                                            account_id BIGINT NOT NULL,
                                            category_id BIGINT NULL,
                                            `date` DATE NOT NULL,
                                            amount DECIMAL(14,2) NOT NULL,
    tx_type ENUM('INCOME','EXPENSE','TRANSFER') NOT NULL,
    status ENUM('PLANNED','PAID','CANCELLED') NOT NULL DEFAULT 'PAID',
    description VARCHAR(255) NULL,
    counterpart_account_id BIGINT NULL,
    linked_transaction_id BIGINT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_tx_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_tx_account FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE,
    CONSTRAINT fk_tx_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL,
    CONSTRAINT fk_tx_counterpart_account FOREIGN KEY (counterpart_account_id) REFERENCES accounts(id) ON DELETE SET NULL,
    INDEX idx_tx_user_date (user_id, `date`),
    INDEX idx_tx_user_account (user_id, account_id),
    INDEX idx_tx_user_category (user_id, category_id),
    INDEX idx_tx_linked (linked_transaction_id)
    ) ENGINE=InnoDB;

-- ============================
-- Tabela: budgets
-- ============================
CREATE TABLE IF NOT EXISTS budgets (
                                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                       user_id BIGINT NOT NULL,
                                       category_id BIGINT NOT NULL,
                                       period_month DATE NOT NULL,
                                       limit_amount DECIMAL(14,2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_budgets_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_budgets_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE,
    CONSTRAINT uq_budget_user_category_month UNIQUE (user_id, category_id, period_month)
    ) ENGINE=InnoDB;

-- ============================
-- Tabela: goals
-- ============================
CREATE TABLE IF NOT EXISTS goals (
                                     id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                     user_id BIGINT NOT NULL,
                                     name VARCHAR(160) NOT NULL,
    target_amount DECIMAL(14,2) NOT NULL,
    current_amount DECIMAL(14,2) NOT NULL DEFAULT 0.00,
    target_date DATE NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_goals_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    ) ENGINE=InnoDB;

-- ============================
-- Tabela: recurring_transactions
-- ============================
CREATE TABLE IF NOT EXISTS recurring_transactions (
                                                      id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                                      user_id BIGINT NOT NULL,
                                                      account_id BIGINT NOT NULL,
                                                      category_id BIGINT NULL,
                                                      recurring_type ENUM('INCOME','EXPENSE','TRANSFER') NOT NULL,
    amount DECIMAL(14,2) NOT NULL,
    description VARCHAR(255) NULL,
    frequency ENUM('DAILY','WEEKLY','MONTHLY','YEARLY') NOT NULL,
    interval_value INT NOT NULL DEFAULT 1,
    start_date DATE NOT NULL,
    end_date DATE NULL,
    next_run_date DATE NOT NULL,
    counterpart_account_id BIGINT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_rec_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_rec_account FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE,
    CONSTRAINT fk_rec_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL,
    CONSTRAINT fk_rec_counterpart_account FOREIGN KEY (counterpart_account_id) REFERENCES accounts(id) ON DELETE SET NULL,
    INDEX idx_rec_user_next (user_id, next_run_date)
    ) ENGINE=InnoDB;

-- ============================
-- View: v_account_balances
-- ============================
CREATE OR REPLACE VIEW v_account_balances AS
SELECT
    a.id AS account_id,
    a.user_id,
    a.name AS account_name,
    a.currency,
    a.opening_balance
        + COALESCE((
                       SELECT SUM(t.amount)
                       FROM transactions t
                       WHERE t.account_id = a.id
                         AND t.user_id = a.user_id
                         AND t.status = 'PAID'
                   ), 0) AS current_balance
FROM accounts a;

-- ============================
-- View: v_monthly_summary
-- ============================
CREATE OR REPLACE VIEW v_monthly_summary AS
SELECT
    user_id,
    DATE_FORMAT(`date`, '%Y-%m-01') AS period_month,
    SUM(CASE WHEN tx_type='INCOME' THEN amount ELSE 0 END) AS income_total,
    SUM(CASE WHEN tx_type='EXPENSE' THEN -amount ELSE 0 END) AS expense_total,
    SUM(CASE
            WHEN tx_type='INCOME' THEN amount
            WHEN tx_type='EXPENSE' THEN -amount
            ELSE 0
        END) AS net_total
FROM transactions
WHERE status <> 'CANCELLED'
GROUP BY user_id, DATE_FORMAT(`date`, '%Y-%m-01');
