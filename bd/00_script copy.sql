DROP DATABASE IF EXISTS vroomer_db;
CREATE DATABASE vroomer_db;
\c vroomer_db;

-- Table client
DELETE FROM reservation;
DELETE FROM hotel;
DELETE FROM client;

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
('Colbert'),
('Novotel'),
('Ibis'),
('Lokanga');

-- Insertion de réservations (aléatoires)
INSERT INTO reservation (id_client, nb_passager, dateheure, id_hotel) VALUES
(1, 11, '2026-02-05 00:01:00', 3),
(2, 1, '2026-02-05 23:55:00', 3),
(3, 2, '2026-02-09 10:17:00', 1),
(4, 4, '2026-02-01 15:25:00', 2),
(5, 4, '2026-01-28 07:11:00', 1),
(6, 5, '2026-01-28 07:45:00', 1),
(7, 13, '2026-02-28 08:25:00', 2),
(8, 8, '2026-02-28 13:00:00', 2),
(9, 7, '2026-02-15 13:00:00', 1),
(10, 1, '2026-02-18 22:55:00', 4);



