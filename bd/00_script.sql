DROP DATABASE IF EXISTS vroomer_db;
CREATE DATABASE vroomer_db;
\c vroomer_db;

-- Table client
DROP TABLE IF EXISTS reservation;
DROP TABLE IF EXISTS hotel;
DROP TABLE IF EXISTS client;
CREATE TABLE client (
	id SERIAL PRIMARY KEY,
	nom VARCHAR(100) NOT NULL
);

-- Table hotel
CREATE TABLE hotel (
	id SERIAL PRIMARY KEY,
	nom VARCHAR(100) NOT NULL
);

-- Table reservation
CREATE TABLE reservation (
	id SERIAL PRIMARY KEY,
	id_client INTEGER REFERENCES client(id) ON DELETE CASCADE,
	nb_passager INTEGER NOT NULL,
	dateheure TIMESTAMP NOT NULL,
	id_hotel INTEGER REFERENCES hotel(id) ON DELETE CASCADE
);

-- Insertion de clients
INSERT INTO client (nom) VALUES
('Alice Martin'),
('Bob Dupont'),
('Charlie Durand'),
('Diane Petit'),
('Eric Leroy'),
('Fatou Ndiaye'),
('Georges Blanc'),
('Hélène Moreau'),
('Ismael Traoré'),
('Julie Bernard'),
('Kevin Rousseau'),
('Laura Girard'),
('Mamadou Sy'),
('Nina Dubois'),
('Olivier Faure'),
('Pauline Lefevre'),
('Quentin Giraud'),
('Rachid Benali'),
('Sophie Laurent'),
('Thomas Muller');

-- Insertion d'hôtels
INSERT INTO hotel (nom) VALUES
('Hotel Central'),
('Grand Palace'),
('Sunset Resort'),
('Ocean View'),
('Mountain Lodge');

-- Insertion de réservations (aléatoires)
INSERT INTO reservation (id_client, nb_passager, dateheure, id_hotel) VALUES
(1, 2, '2026-02-07 10:00:00', 1),
(2, 4, '2026-02-08 12:30:00', 2),
(3, 1, '2026-02-09 09:15:00', 3),
(4, 3, '2026-02-10 14:45:00', 4);

-- =========================
-- TABLE TOKEN (Sécurité API)
-- =========================
CREATE TABLE token (
	id_token SERIAL PRIMARY KEY,
	token VARCHAR(255) NOT NULL UNIQUE,
	datetime_exp TIMESTAMP NOT NULL
);

-- Insertion de tokens de test
INSERT INTO token (token, datetime_exp) VALUES
('FRONT-KEY-PUBLIC', '2027-02-12 23:59:59'),
('FRONT-KEY-DEV', '2027-06-30 23:59:59'),
('FRONT-KEY-TEST', '2027-12-31 23:59:59'),
('FRONT-KEY-EXPIRED', '2025-01-01 00:00:00');
