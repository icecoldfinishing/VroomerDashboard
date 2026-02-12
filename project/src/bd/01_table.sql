DROP DATABASE IF EXISTS pg10;
CREATE DATABASE pg10;
\c pg10;

-- =========================
-- UTILISATEUR (ADMIN)
-- =========================
CREATE TABLE utilisateur (
    id_utilisateur SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    mot_de_passe VARCHAR(100) NOT NULL,
    role VARCHAR(20) DEFAULT 'admin'
);

-- =========================
-- COMPAGNIE
-- =========================
CREATE TABLE compagnie (
    id_compagnie SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    pays VARCHAR(50),
    code_iata CHAR(2) UNIQUE,
    code_icao CHAR(3) UNIQUE
);

-- =========================
-- AEROPORT
-- =========================
CREATE TABLE aeroport (
    id_aeroport SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    ville VARCHAR(50),
    pays VARCHAR(50),
    code_iata CHAR(3) UNIQUE NOT NULL,
    code_icao CHAR(4) UNIQUE
);

-- =========================
-- AVION
-- =========================
CREATE TABLE avion (
    id_avion SERIAL PRIMARY KEY,
    id_compagnie INT NOT NULL,
    modele VARCHAR(50) NOT NULL,
    capacite INT NOT NULL CHECK (capacite > 0),
    numero_immatriculation VARCHAR(20) UNIQUE NOT NULL,
    FOREIGN KEY (id_compagnie) REFERENCES compagnie(id_compagnie)
);

-- =========================
-- SIEGE (Lié à Avion maintenant)
-- =========================
CREATE TABLE siege (
    id_siege SERIAL PRIMARY KEY,
    id_avion INT NOT NULL,    
    numero VARCHAR(5) NOT NULL,           
    classe VARCHAR(20) NOT NULL CHECK (classe IN ('ECONOMY','PREMIUM','FIRST')),
    FOREIGN KEY (id_avion) REFERENCES avion(id_avion),
    UNIQUE (id_avion, numero)
);

-- =========================
-- VOL (trajet théorique)
-- =========================
CREATE TABLE vol (
    id_vol SERIAL PRIMARY KEY,
    id_compagnie INT NOT NULL,
    id_aeroport_depart INT NOT NULL,
    id_aeroport_arrivee INT NOT NULL,
    FOREIGN KEY (id_compagnie) REFERENCES compagnie(id_compagnie),
    FOREIGN KEY (id_aeroport_depart) REFERENCES aeroport(id_aeroport),
    FOREIGN KEY (id_aeroport_arrivee) REFERENCES aeroport(id_aeroport),
    CHECK (id_aeroport_depart <> id_aeroport_arrivee)
);

-- =========================
-- VOL INSTANCE (date réelle)
-- =========================
CREATE TABLE vol_instance (
    id_vol_instance SERIAL PRIMARY KEY,
    id_vol INT NOT NULL,
    id_avion INT NOT NULL,
    date_depart TIMESTAMP NOT NULL,
    date_arrivee TIMESTAMP NOT NULL,
    statut VARCHAR(20) DEFAULT 'PREVU',
    FOREIGN KEY (id_vol) REFERENCES vol(id_vol),
    FOREIGN KEY (id_avion) REFERENCES avion(id_avion)
);

-- =========================
-- SIEGE_VOL (Instance de siège pour un vol donné)
-- =========================
CREATE TABLE siege_vol (
    id_siege_vol SERIAL PRIMARY KEY,
    id_vol_instance INT NOT NULL,
    id_siege INT NOT NULL,
    statut VARCHAR(20) DEFAULT 'LIBRE', -- LIBRE, OCCUPE
    FOREIGN KEY (id_vol_instance) REFERENCES vol_instance(id_vol_instance),
    FOREIGN KEY (id_siege) REFERENCES siege(id_siege),
    UNIQUE (id_vol_instance, id_siege)
);

-- =========================
-- TARIF VOL (Fusion PrixVol + Promotion, lié à VolInstance)
-- =========================
CREATE TABLE tarif_vol (
    id_tarif SERIAL PRIMARY KEY,
    id_vol_instance INT NOT NULL,
    classe VARCHAR(20) NOT NULL CHECK (classe IN ('ECONOMY','PREMIUM','FIRST')),
    type_passager VARCHAR(20) NOT NULL CHECK (type_passager IN ('ADULTE', 'ENFANT', 'BEBE')),
    montant NUMERIC(10,2) NOT NULL CHECK (montant >= 0),
    FOREIGN KEY (id_vol_instance) REFERENCES vol_instance(id_vol_instance),
    UNIQUE (id_vol_instance, classe, type_passager)
);

-- =========================
-- PASSAGER
-- =========================
CREATE TABLE passager (
    id_passager SERIAL PRIMARY KEY,
    nom VARCHAR(50) NOT NULL,
    prenom VARCHAR(50) NOT NULL,
    date_naissance DATE,
    email VARCHAR(100) ,
    type_passager VARCHAR(20) NOT NULL DEFAULT 'ADULTE' CHECK (type_passager IN ('ADULTE', 'ENFANT', 'BEBE'))
);


-- =========================
-- RESERVATION
-- =========================
CREATE TABLE reservation (
    id_reservation SERIAL PRIMARY KEY,
    id_passager INT NOT NULL,
    id_vol_instance INT NOT NULL,
    id_tarif INT NOT NULL,
    id_siege_vol INT NOT NULL, -- Référence au siège spécifique du vol
    date_reservation TIMESTAMP DEFAULT NOW(),
    statut VARCHAR(20) DEFAULT 'CONFIRMEE',
    FOREIGN KEY (id_passager) REFERENCES passager(id_passager),
    FOREIGN KEY (id_vol_instance) REFERENCES vol_instance(id_vol_instance),
    FOREIGN KEY (id_tarif) REFERENCES tarif_vol(id_tarif),
    FOREIGN KEY (id_siege_vol) REFERENCES siege_vol(id_siege_vol)
);

-- =========================
-- PILOTE
-- =========================
CREATE TABLE pilote (
    id_pilote SERIAL PRIMARY KEY,
    id_compagnie INT NOT NULL,
    nom VARCHAR(50) NOT NULL,
    prenom VARCHAR(50) NOT NULL,
    licence_num VARCHAR(20) UNIQUE NOT NULL,
    experience INT DEFAULT 0,
    FOREIGN KEY (id_compagnie) REFERENCES compagnie(id_compagnie)
);

-- =========================
-- EQUIPAGE
-- =========================
CREATE TABLE equipage (
    id_equipage SERIAL PRIMARY KEY,
    id_vol_instance INT NOT NULL,
    id_pilote INT NOT NULL,
    role VARCHAR(50) DEFAULT 'PILOTE',
    FOREIGN KEY (id_vol_instance) REFERENCES vol_instance(id_vol_instance),
    FOREIGN KEY (id_pilote) REFERENCES pilote(id_pilote),
    UNIQUE (id_vol_instance, id_pilote)
);


-- =========================
-- MODE PAIEMENT
-- =========================
CREATE TABLE moyen_paiement (
    id_moyen_paiement SERIAL PRIMARY KEY,
    libelle VARCHAR(50) NOT NULL UNIQUE
);

-- =========================
-- PAIEMENT
-- =========================
CREATE TABLE paiement (
    id_paiement SERIAL PRIMARY KEY,
    id_reservation INT NOT NULL,
    id_moyen_paiement INT NOT NULL,
    montant NUMERIC(10,2) NOT NULL,
    date_paiement TIMESTAMP DEFAULT NOW(),
    statut VARCHAR(20) DEFAULT 'OK',
    FOREIGN KEY (id_reservation) REFERENCES reservation(id_reservation),
    FOREIGN KEY (id_moyen_paiement) REFERENCES moyen_paiement(id_moyen_paiement)
);
