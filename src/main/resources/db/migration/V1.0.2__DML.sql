-- =========================================================================
-- V1.0.2__DML.sql : INITIALISATION DU RÉFÉRENTIEL ET JEU DE DONNÉES (JDD)
-- =========================================================================

-- =========================================================================
-- 1. NETTOYAGE DES TABLES
-- =========================================================================

TRUNCATE TABLE
    paiement,
    quittance,
    etat_les_lieux,
    type_constat,
    charge,
    revision,
    bail_historique,
    bail,
    appartement,
    immeuble,
    locataire,
    proprietaire
RESTART IDENTITY CASCADE;

-- =========================================================================
-- 2. RÉFÉRENTIELS (PARAMÈTRES FIXES)
-- =========================================================================

INSERT INTO type_constat (id, libelle) VALUES
    (1, 'ENTREE'),
    (2, 'SORTIE'),
    (3, 'INTERMEDIAIRE')
ON CONFLICT (id) DO UPDATE SET libelle = EXCLUDED.libelle;

-- =========================================================================
-- 3. JEU DE DONNÉES DE TEST (JDD)
-- =========================================================================

-- 3.1 Propriétaires (20)
INSERT INTO proprietaire (id, nom, prenom, numero, email, adresse)
SELECT
    n AS id,
    CASE n % 5
        WHEN 0 THEN 'Diallo'
        WHEN 1 THEN 'Barry'
        WHEN 2 THEN 'Camara'
        WHEN 3 THEN 'Traore'
        ELSE 'Sow'
    END || '_' || n,
    CASE n % 4
        WHEN 0 THEN 'Amadou'
        WHEN 1 THEN 'Fatou'
        WHEN 2 THEN 'Mamadou'
        ELSE 'Aissatou'
    END,
    '+22462' || LPAD(n::text, 7, '0'),
    'proprietaire' || n || '@gestionlocative.com',
    'Rue ' || (n * 12) || ', Kaloum, Conakry'
FROM generate_series(1, 20) AS n
ON CONFLICT (id) DO NOTHING;

-- 3.2 Immeubles (15)
INSERT INTO immeuble (id, nom, adresse, nb_etages, proprietaire_id)
SELECT
    n AS id,
    'Résidence ' || CASE n % 5
        WHEN 0 THEN 'Kante'
        WHEN 1 THEN 'Bellevue'
        WHEN 2 THEN 'Les Palmiers'
        WHEN 3 THEN 'Almamya'
        ELSE 'Oasis'
    END || ' ' || n,
    n || ' Boulevard du Commerce, Quartier ' || n,
    3 + (n % 6), -- Entre 3 et 8 étages
    ((n - 1) % 20) + 1
FROM generate_series(1, 15) AS n
ON CONFLICT (id) DO NOTHING;

-- 3.3 Charges annuelles des immeubles (pour 2024, 2025, 2026)
INSERT INTO charge (id, annee, montant, immeuble_id)
SELECT
    ((i - 1) * 3 + y) AS id,
    2023 + y AS annee,
    (500000 + (i * 25000) + (y * 15000))::numeric(38,2) AS montant,
    i AS immeuble_id
FROM generate_series(1, 15) AS i
CROSS JOIN generate_series(1, 3) AS y
ON CONFLICT (id) DO NOTHING;

-- 3.4 Appartements (60 appartements répartis sur les 15 immeubles)
INSERT INTO appartement (id, nom, surface, nb_pieces, etage, immeuble_id, proprietaire_id, date_creation)
SELECT
    n AS id,
    'Appartement ' || (100 + (n % 40)),
    (30.0 + (n % 6) * 18.5)::numeric(38, 2), -- Surfaces de 30m² à ~120m²
    1 + (n % 4),                            -- De 1 à 4 pièces
    (n % 5) + 1,                            -- Étage de 1 à 5
    ((n - 1) % 15) + 1,                     -- immeuble_id (1 à 15)
    ((n - 1) % 20) + 1,                     -- proprietaire_id
    TIMESTAMP '2023-01-01 00:00:00' + (n || ' days')::interval
FROM generate_series(1, 60) AS n
ON CONFLICT (id) DO NOTHING;

-- 3.5 Locataires (40)
INSERT INTO locataire (id, nom, prenom, numero, email)
SELECT
    n AS id,
    CASE n % 5
        WHEN 0 THEN 'Sylla'
        WHEN 1 THEN 'Bah'
        WHEN 2 THEN 'Fofana'
        WHEN 3 THEN 'Conde'
        ELSE 'Kaba'
    END || '_' || n,
    CASE n % 4
        WHEN 0 THEN 'Mariam'
        WHEN 1 THEN 'Ibrahima'
        WHEN 2 THEN 'Ousmane'
        ELSE 'Kadiatou'
    END,
    '+22466' || LPAD(n::text, 7, '0'),
    'locataire' || n || '@gmail.com'
FROM generate_series(1, 40) AS n
ON CONFLICT (id) DO NOTHING;

-- 3.6 Baux (45 baux)
-- Certains clos/terminés (anciens), certains en cours (actifs), certains sans date de fin
INSERT INTO bail (id, date_debut, date_fin, montant_loyer, provision_charges, depot_garantie, date_cloture, statut, appartement_id, locataire_id)
SELECT
    n AS id,
    CASE 
        WHEN n <= 10 THEN TIMESTAMP '2024-01-01 00:00:00' + (n * 5 || ' days')::interval
        WHEN n <= 35 THEN TIMESTAMP '2025-06-01 00:00:00' + (n * 3 || ' days')::interval
        ELSE TIMESTAMP '2026-01-01 00:00:00' + (n || ' days')::interval
    END AS date_debut,
    CASE 
        WHEN n <= 10 THEN TIMESTAMP '2024-12-31 00:00:00' + (n * 5 || ' days')::interval
        WHEN n <= 30 THEN TIMESTAMP '2027-05-31 00:00:00' + (n * 3 || ' days')::interval
        ELSE NULL -- Baux ouverts (sans date_fin)
    END AS date_fin,
    (250000 + (n % 8) * 50000)::numeric(38, 2) AS montant_loyer,
    (20000 + (n % 4) * 10000)::numeric(38, 2) AS provision_charges,
    (500000 + (n % 8) * 100000)::numeric(38, 2) AS depot_garantie,
    CASE 
        WHEN n <= 10 THEN TIMESTAMP '2025-01-05 00:00:00'
        ELSE NULL 
    END AS date_cloture,
    CASE 
        WHEN n <= 10 THEN 'TERMINE'
        ELSE 'ACTIF'
    END AS statut,
    n AS appartement_id,
    ((n - 1) % 40) + 1 AS locataire_id
FROM generate_series(1, 45) AS n
ON CONFLICT (id) DO NOTHING;

-- 3.7 Révisions de loyer sur certains baux actifs
INSERT INTO revision (id, bail_id, date_effet, montant_loyer)
VALUES
    (1, 11, TIMESTAMP '2026-01-01 00:00:00', 320000.00),
    (2, 12, TIMESTAMP '2026-03-01 00:00:00', 420000.00),
    (3, 15, TIMESTAMP '2026-06-01 00:00:00', 500000.00),
    (4, 20, TIMESTAMP '2026-02-01 00:00:00', 380000.00)
ON CONFLICT (id) DO NOTHING;

-- 3.8 États des lieux
INSERT INTO etat_les_lieux (id, cout_reparation, date_constat, observations, bail_id, type_constat_id)
VALUES
    (1, 0.00,      TIMESTAMP '2024-01-02 10:00:00', 'Appartement en parfait état à la remise des clés', 1, 1),
    (2, 150000.00, TIMESTAMP '2025-01-04 15:00:00', 'Peinture salon à rafraîchir et serrure à remplacer', 1, 2),
    (3, 0.00,      TIMESTAMP '2025-06-05 09:30:00', 'État impeccable', 11, 1),
    (4, 50000.00,  TIMESTAMP '2025-06-10 14:00:00', 'Légère usure robinetterie cuisine', 12, 1)
ON CONFLICT (id) DO NOTHING;

-- 3.9 Quittances de loyer (Génération de plusieurs mois pour les baux en cours)
-- Chaque quittance a montant_loyer_facture et montant_charge_facture
INSERT INTO quittance (id, mois, annee, montant_loyer_facture, montant_charge_facture, bail_id)
SELECT
    ((b - 1) * 6 + m) AS id,
    m AS mois,
    2026 AS annee,
    (SELECT montant_loyer FROM bail WHERE id = b) AS montant_loyer_facture,
    (SELECT provision_charges FROM bail WHERE id = b) AS montant_charge_facture,
    b AS bail_id
FROM generate_series(11, 25) AS b -- 15 baux actifs
CROSS JOIN generate_series(1, 6) AS m -- 6 mois de quittances (Janvier à Juin 2026)
ON CONFLICT (id) DO NOTHING;

-- 3.10 Paiements
-- Pour alimenter avec réalisme v_bail_financier et v_locataire_situation :
-- - Mois 1 à 4 : entièrement payés
-- - Mois 5 : payé partiellement
-- - Mois 6 : non payé (impayé)
INSERT INTO paiement (id, date_paiement, montant_paye, quittance_id)
SELECT
    ROW_NUMBER() OVER () AS id,
    (TIMESTAMP '2026-01-05 10:00:00' + ((q.mois - 1) * 30 || ' days')::interval) AS date_paiement,
    CASE 
        WHEN q.mois <= 4 THEN (q.montant_loyer_facture + q.montant_charge_facture)
        WHEN q.mois = 5 THEN ROUND((q.montant_loyer_facture + q.montant_charge_facture) * 0.5, 2)
        ELSE 0 -- Mois 6 reste impayé
    END AS montant_paye,
    q.id AS quittance_id
FROM quittance q
WHERE q.mois <= 5
ON CONFLICT (id) DO NOTHING;

-- =========================================================================
-- 4. MISE À JOUR DES SÉQUENCES
-- =========================================================================

SELECT setval('proprietaire_id_seq', (SELECT COALESCE(MAX(id), 1) FROM proprietaire));
SELECT setval('immeuble_id_seq', (SELECT COALESCE(MAX(id), 1) FROM immeuble));
SELECT setval('appartement_id_seq', (SELECT COALESCE(MAX(id), 1) FROM appartement));
SELECT setval('locataire_id_seq', (SELECT COALESCE(MAX(id), 1) FROM locataire));
SELECT setval('bail_id_seq', (SELECT COALESCE(MAX(id), 1) FROM bail));
SELECT setval('charge_id_seq', (SELECT COALESCE(MAX(id), 1) FROM charge));
SELECT setval('type_constat_id_seq', (SELECT COALESCE(MAX(id), 1) FROM type_constat));
SELECT setval('etat_les_lieux_id_seq', (SELECT COALESCE(MAX(id), 1) FROM etat_les_lieux));
SELECT setval('quittance_id_seq', (SELECT COALESCE(MAX(id), 1) FROM quittance));
SELECT setval('paiement_id_seq', (SELECT COALESCE(MAX(id), 1) FROM paiement));
SELECT setval('revision_id_seq', (SELECT COALESCE(MAX(id), 1) FROM revision));
SELECT setval('bail_historique_id_seq', (SELECT COALESCE(MAX(id), 1) FROM bail_historique));
