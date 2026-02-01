-- ============================================================================
-- Base de données Vroomer - Location de voitures
-- ============================================================================

DROP DATABASE IF EXISTS vroomer_db;
CREATE DATABASE vroomer_db;

\c vroomer_db;

-- ============================================================================
-- Table: marques (Brands)
-- ============================================================================
CREATE TABLE marques (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    logo VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================================
-- Table: categories (Car Categories)
-- ============================================================================
CREATE TABLE categories (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    description TEXT,
    icone VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================================
-- Table: voitures (Cars)
-- ============================================================================
CREATE TABLE voitures (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(200) NOT NULL,
    marque_id INTEGER REFERENCES marques(id) ON DELETE SET NULL,
    categorie_id INTEGER REFERENCES categories(id) ON DELETE SET NULL,
    prix_jour DECIMAL(10,2) NOT NULL,
    image VARCHAR(255),
    description TEXT,
    annee INTEGER,
    places INTEGER DEFAULT 5,
    portes INTEGER DEFAULT 4,
    transmission VARCHAR(50) DEFAULT 'Automatic',
    carburant VARCHAR(50) DEFAULT 'Diesel',
    kilometrage VARCHAR(50) DEFAULT 'Unlimited',
    disponible BOOLEAN DEFAULT TRUE,
    featured BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================================
-- Table: services
-- ============================================================================
CREATE TABLE services (
    id SERIAL PRIMARY KEY,
    titre VARCHAR(200) NOT NULL,
    description TEXT,
    icone VARCHAR(100),
    prix DECIMAL(10,2),
    actif BOOLEAN DEFAULT TRUE,
    ordre INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================================
-- Table: tarifs (Pricing Plans)
-- ============================================================================
CREATE TABLE tarifs (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prix DECIMAL(10,2) NOT NULL,
    duree VARCHAR(50) NOT NULL, -- 'jour', 'semaine', 'mois'
    description TEXT,
    features TEXT[], -- Array of features
    populaire BOOLEAN DEFAULT FALSE,
    actif BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================================
-- Table: temoignages (Testimonials)
-- ============================================================================
CREATE TABLE temoignages (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    poste VARCHAR(100),
    photo VARCHAR(255),
    commentaire TEXT NOT NULL,
    note INTEGER DEFAULT 5 CHECK (note >= 1 AND note <= 5),
    actif BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================================
-- Table: blogs (Blog Posts)
-- ============================================================================
CREATE TABLE blogs (
    id SERIAL PRIMARY KEY,
    titre VARCHAR(300) NOT NULL,
    contenu TEXT,
    extrait VARCHAR(500),
    image VARCHAR(255),
    auteur VARCHAR(100) DEFAULT 'Admin',
    commentaires_count INTEGER DEFAULT 0,
    date_publication DATE DEFAULT CURRENT_DATE,
    actif BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================================
-- Table: statistiques (Statistics for counter section)
-- ============================================================================
CREATE TABLE statistiques (
    id SERIAL PRIMARY KEY,
    cle VARCHAR(100) UNIQUE NOT NULL,
    valeur INTEGER NOT NULL,
    label VARCHAR(100) NOT NULL,
    icone VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================================
-- Table: reservations (Bookings)
-- ============================================================================
CREATE TABLE reservations (
    id SERIAL PRIMARY KEY,
    voiture_id INTEGER REFERENCES voitures(id) ON DELETE CASCADE,
    nom_client VARCHAR(200) NOT NULL,
    email_client VARCHAR(200),
    telephone VARCHAR(50),
    lieu_depart VARCHAR(300),
    lieu_arrivee VARCHAR(300),
    date_depart DATE NOT NULL,
    date_retour DATE NOT NULL,
    heure_depart TIME,
    statut VARCHAR(50) DEFAULT 'pending', -- pending, confirmed, cancelled, completed
    prix_total DECIMAL(10,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================================
-- Table: contacts (Contact Messages)
-- ============================================================================
CREATE TABLE contacts (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(200) NOT NULL,
    email VARCHAR(200) NOT NULL,
    sujet VARCHAR(300),
    message TEXT NOT NULL,
    lu BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================================
-- Table: configurations (Site Configuration)
-- ============================================================================
CREATE TABLE configurations (
    id SERIAL PRIMARY KEY,
    cle VARCHAR(100) UNIQUE NOT NULL,
    valeur TEXT,
    description VARCHAR(300)
);

-- ============================================================================
-- INSERTION DES DONNÉES DE DÉMONSTRATION
-- ============================================================================

-- Marques
INSERT INTO marques (nom, logo) VALUES
('Mercedes', '/project/images/brands/mercedes.png'),
('BMW', '/project/images/brands/bmw.png'),
('Audi', '/project/images/brands/audi.png'),
('Land Rover', '/project/images/brands/landrover.png'),
('Chevrolet', '/project/images/brands/chevrolet.png'),
('Toyota', '/project/images/brands/toyota.png');

-- Catégories
INSERT INTO categories (nom, description, icone) VALUES
('Sedan', 'Voitures berlines confortables', 'flaticon-car'),
('SUV', 'Véhicules utilitaires sport', 'flaticon-transportation'),
('Luxury', 'Voitures de luxe haut de gamme', 'flaticon-wedding-car'),
('Economy', 'Voitures économiques', 'flaticon-rent');

-- Voitures
INSERT INTO voitures (nom, marque_id, categorie_id, prix_jour, image, description, annee, places, portes, transmission, carburant, disponible, featured) VALUES
('Mercedes Grand Sedan', 1, 1, 500.00, '/project/images/car-1.jpg', 'Luxueuse berline Mercedes avec tout le confort moderne.', 2024, 5, 4, 'Automatic', 'Diesel', TRUE, TRUE),
('Range Rover Sport', 4, 2, 700.00, '/project/images/car-2.jpg', 'SUV premium avec capacités tout-terrain exceptionnelles.', 2024, 7, 5, 'Automatic', 'Diesel', TRUE, TRUE),
('BMW X5', 2, 2, 600.00, '/project/images/car-3.jpg', 'Le SUV de luxe par excellence, alliant puissance et élégance.', 2023, 5, 5, 'Automatic', 'Hybrid', TRUE, TRUE),
('Audi A4', 3, 1, 450.00, '/project/images/car-4.jpg', 'Berline sportive au design raffiné.', 2024, 5, 4, 'Automatic', 'Petrol', TRUE, TRUE),
('Mercedes C-Class', 1, 1, 400.00, '/project/images/car-5.jpg', 'Compacte premium avec technologie de pointe.', 2023, 5, 4, 'Automatic', 'Diesel', TRUE, FALSE),
('Toyota Camry', 6, 1, 300.00, '/project/images/car-6.jpg', 'Fiabilité japonaise avec confort américain.', 2024, 5, 4, 'Automatic', 'Hybrid', TRUE, FALSE),
('BMW 7 Series', 2, 3, 900.00, '/project/images/car-7.jpg', 'Le summum du luxe et de la technologie BMW.', 2024, 5, 4, 'Automatic', 'Electric', TRUE, TRUE),
('Chevrolet Suburban', 5, 2, 550.00, '/project/images/car-8.jpg', 'Grand SUV familial américain.', 2023, 8, 5, 'Automatic', 'Petrol', TRUE, FALSE);

-- Services
INSERT INTO services (titre, description, icone, prix, ordre) VALUES
('Wedding Ceremony', 'Service de location de voitures pour mariages avec chauffeur élégant et décoration florale.', 'flaticon-wedding-car', 500.00, 1),
('City Transfer', 'Transferts urbains rapides et confortables vers toutes les destinations de la ville.', 'flaticon-transportation', 50.00, 2),
('Airport Transfer', 'Service de navette aéroport disponible 24h/24, 7j/7 avec suivi des vols.', 'flaticon-car', 75.00, 3),
('Whole City Tour', 'Découvrez la ville avec notre service de tour guidé en voiture de luxe.', 'flaticon-transportation', 200.00, 4);

-- Tarifs
INSERT INTO tarifs (nom, prix, duree, description, features, populaire) VALUES
('Basic', 25.00, 'jour', 'Parfait pour les courts trajets', ARRAY['Kilométrage limité', 'Assurance de base', 'Support téléphonique'], FALSE),
('Standard', 150.00, 'semaine', 'Idéal pour les vacances', ARRAY['Kilométrage illimité', 'Assurance complète', 'Support 24/7', 'GPS inclus'], TRUE),
('Premium', 500.00, 'mois', 'Pour les besoins professionnels', ARRAY['Kilométrage illimité', 'Assurance premium', 'Support VIP', 'GPS inclus', 'Chauffeur optionnel', 'Véhicule de remplacement'], FALSE);

-- Témoignages
INSERT INTO temoignages (nom, poste, photo, commentaire, note) VALUES
('Roger Scott', 'Marketing Manager', '/project/images/person_1.jpg', 'Service exceptionnel ! La voiture était impeccable et le processus de réservation très simple. Je recommande vivement Carbook.', 5),
('Marie Dupont', 'Interface Designer', '/project/images/person_2.jpg', 'J''ai loué une Mercedes pour mon mariage et tout était parfait. Le chauffeur était professionnel et ponctuel.', 5),
('Jean Martin', 'UI Designer', '/project/images/person_3.jpg', 'Excellent rapport qualité-prix. Les voitures sont récentes et bien entretenues. Je suis client régulier maintenant.', 5),
('Sophie Bernard', 'CEO', '/project/images/person_4.jpg', 'Le service VIP est vraiment à la hauteur de nos attentes. Parfait pour nos déplacements professionnels.', 5);

-- Blogs
INSERT INTO blogs (titre, contenu, extrait, image, auteur, commentaires_count, date_publication) VALUES
('Why Lead Generation is Key for Business Growth', 'Le contenu complet de l''article sur la génération de leads...', 'Découvrez pourquoi la génération de leads est essentielle pour la croissance de votre entreprise de location de voitures.', '/project/images/image_1.jpg', 'Admin', 3, '2024-10-29'),
('Top 10 Road Trip Destinations', 'Le contenu complet de l''article sur les road trips...', 'Les meilleures destinations pour un road trip inoubliable avec nos véhicules de location.', '/project/images/image_2.jpg', 'Admin', 5, '2024-10-25'),
('How to Choose the Perfect Rental Car', 'Le contenu complet de l''article sur le choix de voiture...', 'Guide complet pour choisir le véhicule de location idéal selon vos besoins.', '/project/images/image_3.jpg', 'Admin', 2, '2024-10-20');

-- Statistiques
INSERT INTO statistiques (cle, valeur, label, icone) VALUES
('years_experience', 60, 'Années d''expérience', 'icon-award'),
('total_cars', 1090, 'Voitures disponibles', 'icon-car'),
('happy_customers', 2590, 'Clients satisfaits', 'icon-smile'),
('total_branches', 67, 'Agences', 'icon-map');

-- Configurations
INSERT INTO configurations (cle, valeur, description) VALUES
('site_name', 'CarBook', 'Nom du site'),
('site_phone', '+2 392 3929 210', 'Téléphone de contact'),
('site_email', 'info@carbook.com', 'Email de contact'),
('site_address', '203 Fake St. Mountain View, San Francisco, California, USA', 'Adresse'),
('facebook_url', 'https://facebook.com/carbook', 'URL Facebook'),
('twitter_url', 'https://twitter.com/carbook', 'URL Twitter'),
('instagram_url', 'https://instagram.com/carbook', 'URL Instagram');

-- ============================================================================
-- Index pour améliorer les performances
-- ============================================================================
CREATE INDEX idx_voitures_disponible ON voitures(disponible);
CREATE INDEX idx_voitures_featured ON voitures(featured);
CREATE INDEX idx_voitures_marque ON voitures(marque_id);
CREATE INDEX idx_voitures_categorie ON voitures(categorie_id);
CREATE INDEX idx_reservations_voiture ON reservations(voiture_id);
CREATE INDEX idx_reservations_statut ON reservations(statut);
CREATE INDEX idx_blogs_actif ON blogs(actif);
CREATE INDEX idx_services_actif ON services(actif);
