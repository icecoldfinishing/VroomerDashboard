-- =========================
-- TABLE TOKEN (Sécurité API)
-- =========================
CREATE TABLE IF NOT EXISTS token (
    id_token SERIAL PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    datetime_exp TIMESTAMP NOT NULL
);

-- Insertion de tokens de test
-- Token valide (expire dans 1 an)
INSERT INTO token (token, datetime_exp) VALUES
('FRONT-KEY-PUBLIC', '2027-02-12 23:59:59'),
('FRONT-KEY-DEV', '2027-06-30 23:59:59'),
('FRONT-KEY-TEST', '2027-12-31 23:59:59');

-- Token expiré (pour tester le refus)
INSERT INTO token (token, datetime_exp) VALUES
('FRONT-KEY-EXPIRED', '2025-01-01 00:00:00');
