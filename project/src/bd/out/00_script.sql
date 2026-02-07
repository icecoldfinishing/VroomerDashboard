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


-- =========================
-- VUE CHIFFRE D'AFFAIRE
-- =========================
CREATE OR REPLACE VIEW view_chiffre_affaire AS
SELECT 
    c.id_compagnie,
    c.nom AS nom_compagnie,
    a.id_avion,
    a.modele AS avion_modele,
    a.numero_immatriculation,
    v.id_vol,
    vi.id_vol_instance,
    vi.date_depart,
    vi.date_arrivee,
    py.id_paiement,
    py.montant AS montant_paye,
    py.date_paiement,
    py.statut AS statut_paiement
FROM paiement py
JOIN reservation r ON py.id_reservation = r.id_reservation
JOIN vol_instance vi ON r.id_vol_instance = vi.id_vol_instance
JOIN vol v ON vi.id_vol = v.id_vol
JOIN avion a ON vi.id_avion = a.id_avion
JOIN compagnie c ON a.id_compagnie = c.id_compagnie
ORDER BY c.id_compagnie, vi.date_depart, v.id_vol;


-- =========================
-- SOCIETE (Publicité)
-- =========================
CREATE TABLE societe (
    id_societe SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL UNIQUE
);

-- =========================
-- TARIF PUBLICITAIRE
-- =========================
CREATE TABLE tarif_publicitaire (
    id_tarif_pub SERIAL PRIMARY KEY,
    id_compagnie INT NOT NULL,
    montant NUMERIC(10,2) NOT NULL,
    FOREIGN KEY (id_compagnie) REFERENCES compagnie(id_compagnie)
);

-- =========================
-- DIFFUSION (Publicité)
-- =========================
CREATE TABLE diffusion (
    id_diffusion SERIAL PRIMARY KEY,
    id_societe INT NOT NULL,
    id_vol_instance INT NOT NULL,
    id_tarif_pub INT NOT NULL,
    date_diffusion DATE NOT NULL,
    nombre INT NOT NULL CHECK (nombre >= 0),
    FOREIGN KEY (id_societe) REFERENCES societe(id_societe),
    FOREIGN KEY (id_vol_instance) REFERENCES vol_instance(id_vol_instance),
    FOREIGN KEY (id_tarif_pub) REFERENCES tarif_publicitaire(id_tarif_pub)
);

-- =========================
-- PAIEMENT PUBLICITE
-- =========================
CREATE TABLE paiement_publicite (
    id_paiement SERIAL PRIMARY KEY,
    id_diffusion INT NOT NULL,
    date_paiement DATE NOT NULL,
    montant NUMERIC(15,2) NOT NULL CHECK (montant > 0),
    FOREIGN KEY (id_diffusion) REFERENCES diffusion(id_diffusion)
);

-- =============================================================
-- INSERTIONS DE DONNEES DE TEST
-- =============================================================

-- UTILISATEUR
INSERT INTO utilisateur (username, mot_de_passe, role) VALUES
('admin', 'admin', 'admin');

-- AEROPORTS
INSERT INTO aeroport (nom, ville, pays, code_iata, code_icao) VALUES
('Ivato International Airport', 'Antananarivo', 'Madagascar', 'TNR', 'FMMI'),
('Fascene Airport', 'Nosy Be', 'Madagascar', 'NOS', 'FMNN'),
('Arrachart Airport', 'Antsiranana', 'Madagascar', 'DIE', 'FMNA'),
('Toliara Airport', 'Toliara', 'Madagascar', 'TLE', 'FMST'),
('Marillac Airport', 'Fort Dauphin', 'Madagascar', 'FTU', 'FMSD');

-- COMPAGNIES
INSERT INTO compagnie (nom, pays, code_iata, code_icao) VALUES
('Air Madagascar', 'Madagascar', 'MD', 'MDG'),
('Tsaradia', 'Madagascar', 'TZ', 'TSD'),
('Madagascar Airlines', 'Madagascar', 'MA', 'MDA'),
('Ewa Air', 'Comores', 'ZW', 'EWR');

-- AVIONS
INSERT INTO avion (id_compagnie, modele, capacite, numero_immatriculation) VALUES
(1, 'ATR-045', 70, '5R-MJG'),     -- id_avion = 1
(2, 'ATR 42', 48, '5R-TSA'),     -- id_avion = 2
(3, 'Boeing 737', 140, '5R-MDL'),-- id_avion = 3
(1, 'Dash 8 Q400', 78, '5R-DQ4'), -- id_avion = 4
(4, 'Embraer 190', 100, 'D2-EWA'); -- id_avion = 5

-- VOLS (trajets)
INSERT INTO vol (id_compagnie, id_aeroport_depart, id_aeroport_arrivee) VALUES
(1, 1, 2), -- id_vol = 1 (Tana -> Nosy Be)
(2, 1, 2),
(1, 1, 3);

-- VOL INSTANCES
INSERT INTO vol_instance (id_vol, id_avion, date_depart, date_arrivee) VALUES
(1, 1, '2026-01-20 10:00', '2026-01-20 13:00'), -- id_vol_instance = 1 uses id_avion = 1
(1, 1, '2026-01-21 10:00', '2026-01-21 13:00'),
(1, 1, '2026-01-21 15:00', '2026-01-21 18:00');


-- MOYENS PAIEMENT
INSERT INTO moyen_paiement (libelle) VALUES
('CB'), ('Virement'), ('Paypal'), ('Espèces'), ('Chèque');


--societe 1 vaniala
--societe 2 lewis

-- SOCIETES
INSERT INTO societe (nom) VALUES ('Vaniala'), ('Lewis') , ('Socobis') , ('Jejoo');

-- TARIF PUBLICITAIRE
-- Air Madagascar (id_compagnie = 1) : 400.000 Ar par diffusion
INSERT INTO tarif_publicitaire (id_compagnie, montant) VALUES 
(1, 400000);

--dif vaniala
INSERT INTO diffusion (id_societe, id_vol_instance, id_tarif_pub, date_diffusion, nombre) VALUES 
(1, 1, 1, '2026-01-20', 1);
--dif lewis
INSERT INTO diffusion (id_societe, id_vol_instance, id_tarif_pub, date_diffusion, nombre) VALUES 
(2, 1, 1, '2026-01-20', 1);
--dif Socobis
INSERT INTO diffusion (id_societe, id_vol_instance, id_tarif_pub, date_diffusion, nombre) VALUES 
(3, 2, 1, '2026-01-21', 2);
--dif Jejoo
INSERT INTO diffusion (id_societe, id_vol_instance, id_tarif_pub, date_diffusion, nombre) VALUES 
(4, 2, 1, '2026-01-21', 1);

--paiement id1 vaniala
--paiement id2 lewis
INSERT INTO paiement_publicite (id_diffusion, date_paiement, montant) VALUES 
(1, '2026-01-20', 400000),
(2, '2026-01-20', 400000),
(3, '2026-01-20', 800000),
(4, '2026-01-20', 400000);



-- =========================
-- SIEGES pour AVION 1 (lié à id_avion = 1, utilisé par Vol Instance 1)
-- =========================

-- FIRST CLASS (30 sièges)
INSERT INTO siege (id_avion, numero, classe) VALUES
(1,'1A','FIRST'), (1,'1B','FIRST'),
(1,'2A','FIRST'), (1,'2B','FIRST'),
(1,'3A','FIRST'), (1,'3B','FIRST'),
(1,'4A','FIRST'), (1,'4B','FIRST'),
(1,'5A','FIRST'), (1,'5B','FIRST'),
(1,'6A','FIRST'), (1,'6B','FIRST'),
(1,'7A','FIRST'), (1,'7B','FIRST'),
(1,'8A','FIRST'), (1,'8B','FIRST'),
(1,'9A','FIRST'), (1,'9B','FIRST'),
(1,'10A','FIRST'), (1,'10B','FIRST'),
(1,'11A','FIRST'), (1,'11B','FIRST'),
(1,'12A','FIRST'), (1,'12B','FIRST'),
(1,'13A','FIRST'), (1,'13B','FIRST'),
(1,'14A','FIRST'), (1,'14B','FIRST'),
(1,'15A','FIRST'), (1,'15B','FIRST');

-- PREMIUM CLASS (40 sièges)
INSERT INTO siege (id_avion, numero, classe) VALUES
(1,'16A','PREMIUM'), (1,'16B','PREMIUM'),
(1,'17A','PREMIUM'), (1,'17B','PREMIUM'),
(1,'18A','PREMIUM'), (1,'18B','PREMIUM'),
(1,'19A','PREMIUM'), (1,'19B','PREMIUM'),
(1,'20A','PREMIUM'), (1,'20B','PREMIUM'),
(1,'21A','PREMIUM'), (1,'21B','PREMIUM'),
(1,'22A','PREMIUM'), (1,'22B','PREMIUM'),
(1,'23A','PREMIUM'), (1,'23B','PREMIUM'),
(1,'24A','PREMIUM'), (1,'24B','PREMIUM'),
(1,'25A','PREMIUM'), (1,'25B','PREMIUM'),
(1,'26A','PREMIUM'), (1,'26B','PREMIUM'),
(1,'27A','PREMIUM'), (1,'27B','PREMIUM'),
(1,'28A','PREMIUM'), (1,'28B','PREMIUM'),
(1,'29A','PREMIUM'), (1,'29B','PREMIUM'),
(1,'30A','PREMIUM'), (1,'30B','PREMIUM'),
(1,'31A','PREMIUM'), (1,'31B','PREMIUM'),
(1,'32A','PREMIUM'), (1,'32B','PREMIUM'),
(1,'33A','PREMIUM'), (1,'33B','PREMIUM'),
(1,'34A','PREMIUM'), (1,'34B','PREMIUM'),
(1,'35A','PREMIUM'), (1,'35B','PREMIUM');

-- ECONOMY CLASS (50 sièges)
INSERT INTO siege (id_avion, numero, classe) VALUES
(1,'36A','ECONOMY'), (1,'36B','ECONOMY'),
(1,'37A','ECONOMY'), (1,'37B','ECONOMY'),
(1,'38A','ECONOMY'), (1,'38B','ECONOMY'),
(1,'39A','ECONOMY'), (1,'39B','ECONOMY'),
(1,'40A','ECONOMY'), (1,'40B','ECONOMY'),
(1,'41A','ECONOMY'), (1,'41B','ECONOMY'),
(1,'42A','ECONOMY'), (1,'42B','ECONOMY'),
(1,'43A','ECONOMY'), (1,'43B','ECONOMY'),
(1,'44A','ECONOMY'), (1,'44B','ECONOMY'),
(1,'45A','ECONOMY'), (1,'45B','ECONOMY'),
(1,'46A','ECONOMY'), (1,'46B','ECONOMY'),
(1,'47A','ECONOMY'), (1,'47B','ECONOMY'),
(1,'48A','ECONOMY'), (1,'48B','ECONOMY'),
(1,'49A','ECONOMY'), (1,'49B','ECONOMY'),
(1,'50A','ECONOMY'), (1,'50B','ECONOMY'),
(1,'51A','ECONOMY'), (1,'51B','ECONOMY'),
(1,'52A','ECONOMY'), (1,'52B','ECONOMY'),
(1,'53A','ECONOMY'), (1,'53B','ECONOMY'),
(1,'54A','ECONOMY'), (1,'54B','ECONOMY'),
(1,'55A','ECONOMY'), (1,'55B','ECONOMY'),
(1,'56A','ECONOMY'), (1,'56B','ECONOMY'),
(1,'57A','ECONOMY'), (1,'57B','ECONOMY'),
(1,'58A','ECONOMY'), (1,'58B','ECONOMY'),
(1,'59A','ECONOMY'), (1,'59B','ECONOMY'),
(1,'60A','ECONOMY'), (1,'60B','ECONOMY');


-- =========================
-- INITIALISATION SIEGE_VOL pour VOL INSTANCE 1
-- (On insère tous les sièges de l'avion 1 comme LIBRE pour l'instance 1)
-- =========================
INSERT INTO siege_vol (id_vol_instance, id_siege)
SELECT 1, id_siege FROM siege WHERE id_avion = 1 ORDER BY id_siege;


-- =========================
-- TARIFS pour VOL INSTANCE 1
-- =========================
-- ECONOMY
INSERT INTO tarif_vol (id_vol_instance, classe, type_passager, montant) VALUES
(1, 'ECONOMY', 'ADULTE', 800000), 
(1, 'ECONOMY', 'ENFANT', 600000),
(1, 'ECONOMY', 'BEBE', 80000);

-- FIRST
INSERT INTO tarif_vol (id_vol_instance, classe, type_passager, montant) VALUES
(1, 'FIRST', 'ADULTE', 2000000), 
(1, 'FIRST', 'ENFANT', 800000),
(1, 'FIRST', 'BEBE', 200000);    

-- PREMIUM
INSERT INTO tarif_vol (id_vol_instance, classe, type_passager, montant) VALUES
(1, 'PREMIUM', 'ADULTE', 1000000),
(1, 'PREMIUM', 'ENFANT', 700000),
(1, 'PREMIUM', 'BEBE', 100000);    

-- =========================
-- PAIEMENTS PUBLICITE
-- =========================
-- Vaniala (id_societe = 1) : 1.000.000 Ar le 15/12/2025
-- On le lie à la première diffusion de Vaniala (id_societe=1, id_vol_instance=4)
-- NOTE: id_diffusion SERIAL might start at 1, but to be safe in SQL script, 
-- we link to a diffusion that we know exists for Vaniala.
-- In the script, Vaniala's first diffusion is: (1, 4, 1, '2025-12-01', 10)


