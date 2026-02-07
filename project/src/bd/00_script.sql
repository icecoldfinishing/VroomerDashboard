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

    CREATE TABLE produit (
        id_produit SERIAL PRIMARY KEY,
        id_societe INT NOT NULL,
        nom VARCHAR(100) NOT NULL,
        description TEXT,
        prix NUMERIC(10,2) NOT NULL CHECK (prix > 0),
        date_ajout DATE DEFAULT CURRENT_DATE,
        disponible BOOLEAN DEFAULT TRUE,
        FOREIGN KEY (id_societe) REFERENCES societe(id_societe),
        UNIQUE (id_societe, nom)
    );

    -- =========================
    -- VENTE_PRODUIT (Produits vendus par vol instance)
    -- =========================
    CREATE TABLE vente_produit (
        id_vente_produit SERIAL PRIMARY KEY,
        id_vol_instance INT NOT NULL,
        id_produit INT NOT NULL,
        quantite INT NOT NULL CHECK (quantite >= 0),
        date_vente TIMESTAMP DEFAULT NOW(),
        FOREIGN KEY (id_vol_instance) REFERENCES vol_instance(id_vol_instance),
        FOREIGN KEY (id_produit) REFERENCES produit(id_produit),
        UNIQUE (id_vol_instance, id_produit)
    );

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

--dif1 vaniala volI 1
INSERT INTO diffusion (id_societe, id_vol_instance, id_tarif_pub, date_diffusion, nombre) VALUES 
(1, 1, 1, '2026-01-20', 1);
--dif2 lewis volI 1
INSERT INTO diffusion (id_societe, id_vol_instance, id_tarif_pub, date_diffusion, nombre) VALUES 
(2, 1, 1, '2026-01-20', 1);
--dif3 Socobis volI 2
INSERT INTO diffusion (id_societe, id_vol_instance, id_tarif_pub, date_diffusion, nombre) VALUES 
(3, 2, 1, '2026-01-21', 2);
--dif4 Jejoo volI 2
INSERT INTO diffusion (id_societe, id_vol_instance, id_tarif_pub, date_diffusion, nombre) VALUES 
(4, 2, 1, '2026-01-21', 1);


--INSERT INTO paiement_publicite (id_diffusion, date_paiement, montant) VALUES 
--(1, '2026-01-20', 200000);

--paiement id1 vaniala
--paiement id2 lewis
--INSERT INTO paiement_publicite (id_diffusion, date_paiement, montant) VALUES 
--(1, '2026-01-20', 400000),
--(2, '2026-01-20', 400000),
--(3, '2026-01-20', 800000),
--(4, '2026-01-20', 400000);


    -- =========================
    -- CATALOGUE PRODUITS et VENTES par vol_instance
    -- =========================
    -- Produits proposés par les sociétés
INSERT INTO produit (id_societe, nom, prix) VALUES
      (1, 'Tablette de chocolat', 5000);
      --  (2, 'Jus de fruits', 3000);

    -- Ventes par vol_instance (quantités)
    -- Vaniala - Tablette de chocolat
    --INSERT INTO vente_produit (id_vol_instance, id_produit, quantite) VALUES
    -- Vaniala - Tablette de chocolat (id_produit = 1)
    --(1, 1, 50),
    --(2, 1, 40),
    --(3, 1, 30);
    -- Lewis - Jus de fruits (id_produit = 2)
    --(1, 2, 20),
    --(2, 2, 25),
    --(3, 2, 15);


-- =========================
-- SIEGES pour AVION 1 (lié à id_avion = 1, utilisé par Vol Instance 1)
-- =========================
-- ECONOMY CLASS
-- Sièges numérotés de 1 à 300
-- Avion : id_avion = 1

INSERT INTO siege (id_avion, numero, classe) VALUES
(1,'1','ECONOMY'), (1,'2','ECONOMY'), (1,'3','ECONOMY'), (1,'4','ECONOMY'), (1,'5','ECONOMY'),
(1,'6','ECONOMY'), (1,'7','ECONOMY'), (1,'8','ECONOMY'), (1,'9','ECONOMY'), (1,'10','ECONOMY'),
(1,'11','ECONOMY'), (1,'12','ECONOMY'), (1,'13','ECONOMY'), (1,'14','ECONOMY'), (1,'15','ECONOMY'),
(1,'16','ECONOMY'), (1,'17','ECONOMY'), (1,'18','ECONOMY'), (1,'19','ECONOMY'), (1,'20','ECONOMY'),
(1,'21','ECONOMY'), (1,'22','ECONOMY'), (1,'23','ECONOMY'), (1,'24','ECONOMY'), (1,'25','ECONOMY'),
(1,'26','ECONOMY'), (1,'27','ECONOMY'), (1,'28','ECONOMY'), (1,'29','ECONOMY'), (1,'30','ECONOMY'),
(1,'31','ECONOMY'), (1,'32','ECONOMY'), (1,'33','ECONOMY'), (1,'34','ECONOMY'), (1,'35','ECONOMY'),
(1,'36','ECONOMY'), (1,'37','ECONOMY'), (1,'38','ECONOMY'), (1,'39','ECONOMY'), (1,'40','ECONOMY'),
(1,'41','ECONOMY'), (1,'42','ECONOMY'), (1,'43','ECONOMY'), (1,'44','ECONOMY'), (1,'45','ECONOMY'),
(1,'46','ECONOMY'), (1,'47','ECONOMY'), (1,'48','ECONOMY'), (1,'49','ECONOMY'), (1,'50','ECONOMY'),

(1,'51','ECONOMY'), (1,'52','ECONOMY'), (1,'53','ECONOMY'), (1,'54','ECONOMY'), (1,'55','ECONOMY'),
(1,'56','ECONOMY'), (1,'57','ECONOMY'), (1,'58','ECONOMY'), (1,'59','ECONOMY'), (1,'60','ECONOMY'),
(1,'61','ECONOMY'), (1,'62','ECONOMY'), (1,'63','ECONOMY'), (1,'64','ECONOMY'), (1,'65','ECONOMY'),
(1,'66','ECONOMY'), (1,'67','ECONOMY'), (1,'68','ECONOMY'), (1,'69','ECONOMY'), (1,'70','ECONOMY'),
(1,'71','ECONOMY'), (1,'72','ECONOMY'), (1,'73','ECONOMY'), (1,'74','ECONOMY'), (1,'75','ECONOMY'),
(1,'76','ECONOMY'), (1,'77','ECONOMY'), (1,'78','ECONOMY'), (1,'79','ECONOMY'), (1,'80','ECONOMY'),
(1,'81','ECONOMY'), (1,'82','ECONOMY'), (1,'83','ECONOMY'), (1,'84','ECONOMY'), (1,'85','ECONOMY'),
(1,'86','ECONOMY'), (1,'87','ECONOMY'), (1,'88','ECONOMY'), (1,'89','ECONOMY'), (1,'90','ECONOMY'),
(1,'91','ECONOMY'), (1,'92','ECONOMY'), (1,'93','ECONOMY'), (1,'94','ECONOMY'), (1,'95','ECONOMY'),
(1,'96','ECONOMY'), (1,'97','ECONOMY'), (1,'98','ECONOMY'), (1,'99','ECONOMY'), (1,'100','ECONOMY'),

(1,'101','ECONOMY'), (1,'102','ECONOMY'), (1,'103','ECONOMY'), (1,'104','ECONOMY'), (1,'105','ECONOMY'),
(1,'106','ECONOMY'), (1,'107','ECONOMY'), (1,'108','ECONOMY'), (1,'109','ECONOMY'), (1,'110','ECONOMY'),
(1,'111','ECONOMY'), (1,'112','ECONOMY'), (1,'113','ECONOMY'), (1,'114','ECONOMY'), (1,'115','ECONOMY'),
(1,'116','ECONOMY'), (1,'117','ECONOMY'), (1,'118','ECONOMY'), (1,'119','ECONOMY'), (1,'120','ECONOMY'),
(1,'121','ECONOMY'), (1,'122','ECONOMY'), (1,'123','ECONOMY'), (1,'124','ECONOMY'), (1,'125','ECONOMY'),
(1,'126','ECONOMY'), (1,'127','ECONOMY'), (1,'128','ECONOMY'), (1,'129','ECONOMY'), (1,'130','ECONOMY'),
(1,'131','ECONOMY'), (1,'132','ECONOMY'), (1,'133','ECONOMY'), (1,'134','ECONOMY'), (1,'135','ECONOMY'),
(1,'136','ECONOMY'), (1,'137','ECONOMY'), (1,'138','ECONOMY'), (1,'139','ECONOMY'), (1,'140','ECONOMY'),
(1,'141','ECONOMY'), (1,'142','ECONOMY'), (1,'143','ECONOMY'), (1,'144','ECONOMY'), (1,'145','ECONOMY'),
(1,'146','ECONOMY'), (1,'147','ECONOMY'), (1,'148','ECONOMY'), (1,'149','ECONOMY'), (1,'150','ECONOMY'),

(1,'151','ECONOMY'), (1,'152','ECONOMY'), (1,'153','ECONOMY'), (1,'154','ECONOMY'), (1,'155','ECONOMY'),
(1,'156','ECONOMY'), (1,'157','ECONOMY'), (1,'158','ECONOMY'), (1,'159','ECONOMY'), (1,'160','ECONOMY'),
(1,'161','ECONOMY'), (1,'162','ECONOMY'), (1,'163','ECONOMY'), (1,'164','ECONOMY'), (1,'165','ECONOMY'),
(1,'166','ECONOMY'), (1,'167','ECONOMY'), (1,'168','ECONOMY'), (1,'169','ECONOMY'), (1,'170','ECONOMY'),
(1,'171','ECONOMY'), (1,'172','ECONOMY'), (1,'173','ECONOMY'), (1,'174','ECONOMY'), (1,'175','ECONOMY'),
(1,'176','ECONOMY'), (1,'177','ECONOMY'), (1,'178','ECONOMY'), (1,'179','ECONOMY'), (1,'180','ECONOMY'),
(1,'181','ECONOMY'), (1,'182','ECONOMY'), (1,'183','ECONOMY'), (1,'184','ECONOMY'), (1,'185','ECONOMY'),
(1,'186','ECONOMY'), (1,'187','ECONOMY'), (1,'188','ECONOMY'), (1,'189','ECONOMY'), (1,'190','ECONOMY'),
(1,'191','ECONOMY'), (1,'192','ECONOMY'), (1,'193','ECONOMY'), (1,'194','ECONOMY'), (1,'195','ECONOMY'),
(1,'196','ECONOMY'), (1,'197','ECONOMY'), (1,'198','ECONOMY'), (1,'199','ECONOMY'), (1,'200','ECONOMY'),

(1,'201','ECONOMY'), (1,'202','ECONOMY'), (1,'203','ECONOMY'), (1,'204','ECONOMY'), (1,'205','ECONOMY'),
(1,'206','ECONOMY'), (1,'207','ECONOMY'), (1,'208','ECONOMY'), (1,'209','ECONOMY'), (1,'210','ECONOMY'),
(1,'211','ECONOMY'), (1,'212','ECONOMY'), (1,'213','ECONOMY'), (1,'214','ECONOMY'), (1,'215','ECONOMY'),
(1,'216','ECONOMY'), (1,'217','ECONOMY'), (1,'218','ECONOMY'), (1,'219','ECONOMY'), (1,'220','ECONOMY'),
(1,'221','ECONOMY'), (1,'222','ECONOMY'), (1,'223','ECONOMY'), (1,'224','ECONOMY'), (1,'225','ECONOMY'),
(1,'226','ECONOMY'), (1,'227','ECONOMY'), (1,'228','ECONOMY'), (1,'229','ECONOMY'), (1,'230','ECONOMY'),
(1,'231','ECONOMY'), (1,'232','ECONOMY'), (1,'233','ECONOMY'), (1,'234','ECONOMY'), (1,'235','ECONOMY'),
(1,'236','ECONOMY'), (1,'237','ECONOMY'), (1,'238','ECONOMY'), (1,'239','ECONOMY'), (1,'240','ECONOMY'),
(1,'241','ECONOMY'), (1,'242','ECONOMY'), (1,'243','ECONOMY'), (1,'244','ECONOMY'), (1,'245','ECONOMY'),
(1,'246','ECONOMY'), (1,'247','ECONOMY'), (1,'248','ECONOMY'), (1,'249','ECONOMY'), (1,'250','ECONOMY'),

(1,'251','ECONOMY'), (1,'252','ECONOMY'), (1,'253','ECONOMY'), (1,'254','ECONOMY'), (1,'255','ECONOMY'),
(1,'256','ECONOMY'), (1,'257','ECONOMY'), (1,'258','ECONOMY'), (1,'259','ECONOMY'), (1,'260','ECONOMY'),
(1,'261','ECONOMY'), (1,'262','ECONOMY'), (1,'263','ECONOMY'), (1,'264','ECONOMY'), (1,'265','ECONOMY'),
(1,'266','ECONOMY'), (1,'267','ECONOMY'), (1,'268','ECONOMY'), (1,'269','ECONOMY'), (1,'270','ECONOMY'),
(1,'271','ECONOMY'), (1,'272','ECONOMY'), (1,'273','ECONOMY'), (1,'274','ECONOMY'), (1,'275','ECONOMY'),
(1,'276','ECONOMY'), (1,'277','ECONOMY'), (1,'278','ECONOMY'), (1,'279','ECONOMY'), (1,'280','ECONOMY'),
(1,'281','ECONOMY'), (1,'282','ECONOMY'), (1,'283','ECONOMY'), (1,'284','ECONOMY'), (1,'285','ECONOMY'),
(1,'286','ECONOMY'), (1,'287','ECONOMY'), (1,'288','ECONOMY'), (1,'289','ECONOMY'), (1,'290','ECONOMY'),
(1,'291','ECONOMY'), (1,'292','ECONOMY'), (1,'293','ECONOMY'), (1,'294','ECONOMY'), (1,'295','ECONOMY'),
(1,'296','ECONOMY'), (1,'297','ECONOMY'), (1,'298','ECONOMY'), (1,'299','ECONOMY'), (1,'300','ECONOMY');


-- =========================
-- INITIALISATION SIEGE_VOL pour VOL INSTANCE 1
-- (On insère tous les sièges de l'avion 1 comme LIBRE pour l'instance 1)
-- =========================
-- Siege Vol pour id_vol_instance 1 (sièges 1 à 100)
INSERT INTO siege_vol (id_vol_instance, id_siege)
SELECT 1, id_siege
FROM siege
WHERE id_avion = 1 AND id_siege BETWEEN 1 AND 100
ORDER BY id_siege;

-- Siege Vol pour id_vol_instance 2 (sièges 101 à 200)
INSERT INTO siege_vol (id_vol_instance, id_siege)
SELECT 2, id_siege
FROM siege
WHERE id_avion = 1 AND id_siege BETWEEN 101 AND 200
ORDER BY id_siege;

-- Siege Vol pour id_vol_instance 3 (sièges 201 à 300)
INSERT INTO siege_vol (id_vol_instance, id_siege)
SELECT 3, id_siege
FROM siege
WHERE id_avion = 1 AND id_siege BETWEEN 201 AND 300
ORDER BY id_siege;


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





