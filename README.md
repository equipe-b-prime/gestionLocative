# 🏢 Plateforme de Gestion Locative — API REST

Système complet de gestion de patrimoine immobilier locatif développé avec **Spring Boot 4**, **Java 21**, **PostgreSQL** et **Flyway Migration**.

L'application couvre l'ensemble du cycle de vie immobilier : gestion des propriétaires, immeubles, appartements, locataires, baux avec contrôle strict des règles métier (non-chevauchement, durées, résiliation, renouvellement, révision de loyer, clôture automatique) et restitutions analytiques avancées.

---

## 🌐 Environnements & Accès

L'API est accessible et testable aussi bien en ligne qu'en environnement local :

| Environnement | URL de Base | Documentation Interactive (Swagger UI) | Base de Données |
| :--- | :--- | :--- | :--- |
| **En Ligne (Production / Render)** | `https://gestionlocative-2kok.onrender.com` | [Swagger UI en Ligne](https://gestionlocative-2kok.onrender.com/swagger-ui/index.html)<br>*(ou sans index : `/swagger-ui.html`)* | **Neon Cloud PostgreSQL** |
| **Local** | `http://localhost:8080`<br>*(ou `9090`)* | [Swagger UI Local](http://localhost:8080/swagger-ui.html)<br>*(ou `http://localhost:9090/swagger-ui.html`)* | **PostgreSQL Local** |

> [!TIP]
> Sur **Render**, si l'instance est en veille au premier appel, le réveil automatique prend quelques secondes. La documentation Swagger UI est accessible directement via `https://gestionlocative-2kok.onrender.com/swagger-ui/index.html` ou `https://gestionlocative-2kok.onrender.com/swagger-ui.html`.

---

## 📦 Pièce Jointe : Collection Postman Prête à l'Emploi (`postman.json`)

Une collection Postman complète et immédiatement importable est fournie à la racine du dépôt :

📄 **[`postman.json`](./postman.json)** *(également disponible sous le nom [`ZKANTAPI.postman_collection.json`](./ZKANTAPI.postman_collection.json))*

### Comment l'utiliser dans Postman :
1. Ouvrez Postman et cliquez sur le bouton **Import** (en haut à gauche).
2. Sélectionnez le fichier **`postman.json`** (ou `ZKANTAPI.postman_collection.json`).
3. La collection contient tous les cas de tests organisés par niveau de **F1 à F19** (cas passants et cas d'erreur `400`, `404`, `409`).
4. **Bascule facile entre Local et Cloud :** modifiez simplement la variable de collection `baseUrl` :
   - Pour tester en ligne sur Render : `https://gestionlocative-2kok.onrender.com`
   - Pour tester en local : `http://localhost:8080` *(ou `http://localhost:9090`)*

---

## 🚀 Exemples d'appels cURL (En Ligne & En Local)

Vous pouvez tester toutes les routes aussi bien sur le serveur distant que sur votre machine locale :

### 1. Consulter les propriétaires (F1)
```bash
# En ligne (Render)
curl -s "https://gestionlocative-2kok.onrender.com/proprietaires?page=0&size=5"

# En local
curl -s "http://localhost:8080/proprietaires?page=0&size=5"
```

### 2. Lister les appartements libres à une date (F17)
```bash
# En ligne
curl -s "https://gestionlocative-2kok.onrender.com/appartements/libres?date=2026-06-01"

# En local
curl -s "http://localhost:8080/appartements/libres?date=2026-06-01"
```

### 3. Reconstituer l'historique d'occupation d'un appartement (F18)
```bash
# En ligne
curl -s "https://gestionlocative-2kok.onrender.com/appartements/11/occupation"

# En local
curl -s "http://localhost:8080/appartements/11/occupation"
```

### 4. Calculer le revenu mensuel au prorata d'un immeuble (F19)
```bash
# En ligne
curl -s "https://gestionlocative-2kok.onrender.com/immeubles/1/revenu?mois=2026-05"

# En local
curl -s "http://localhost:8080/immeubles/1/revenu?mois=2026-05"
```

### 5. Créer un locataire (F4)
```bash
curl -s -X POST "https://gestionlocative-2kok.onrender.com/locataires" \
  -H "Content-Type: application/json" \
  -d '{
    "nom": "Camara",
    "prenom": "Mariam",
    "numero": "+224621000010",
    "email": "mariam.camara@test.ci"
  }'
```

---

## 🎯 Récapitulatif des Fonctionnalités Métier (F1 à F19)

Le système implémente rigoureusement 19 fonctionnalités réparties en 4 niveaux progressifs :

```
┌────────────────────────────────────────────────────────────────────────┐
│                        GESTION LOCATIVE API                            │
├────────────────────┬────────────────────┬──────────────────────────────┤
│ NIVEAU 1 (CRUD)    │ NIVEAU 2 (RELATIONS│ NIVEAU 3 & 4 (MÉTIER AVANCÉ) │
├────────────────────┼────────────────────┼──────────────────────────────┤
│ F1 : Propriétaires │ F6 : Appts/Immeuble│ F11: Non-chevauchement (409) │
│ F2 : Immeubles     │ F7 : Imm/Proprio   │ F12: Cohérence dates & durée │
│ F3 : Appartements  │ F8 : Baux/Appart   │ F13: Résiliation anticipée   │
│ F4 : Locataires    │ F9 : Multi-critères│ F14: Renouvellement de bail  │
│ F5 : Baux initiaux │ F10: Pagination/Tri│ F15: Révision loyer & histo  │
│                    │                    │ F16: Clôture auto baux       │
│                    │                    │ F17: Appartements libres     │
│                    │                    │ F18: Occupation reconstituée │
│                    │                    │ F19: Revenu & taux immeuble  │
└────────────────────┴────────────────────┴──────────────────────────────┘
```

### Niveau 1 — CRUD & Intégrité
- **F1 — Propriétaires** : gestion complète, unicité email (`409`), contrôles de surface et validations.
- **F2 — Immeubles** : rattachement propriétaire, unicité nom/adresse, validation `nbEtages > 0`.
- **F3 — Appartements** : contrôle de conformité de l'étage (`etage <= nbEtages immeuble`), surfaces positives.
- **F4 — Locataires** : annuaire locataires, contrôle d'unicité email et téléphone.
- **F5 — Baux** : contractualisation locataire-appartement avec montants de loyers et charges.

### Niveau 2 — Relations, Filtres & Pagination
- **F6 — Appartements d'un immeuble** : triés par étage ascendant avec état d'occupation immédiat.
- **F7 — Immeubles d'un propriétaire** : liste et compteurs d'appartements et de baux actifs associés.
- **F8 — Historique des baux d'un appartement** : chronologie complète du plus récent au plus ancien.
- **F9 — Recherche multicritères** : filtre combinable sur `surfaceMin`, `surfaceMax`, `pieces` avec pagination en base.
- **F10 — Pagination et tri des baux** : pagination configurable avec tri multi-colonnes (`dateDebut`, `montantLoyer`, etc.).

### Niveau 3 — Règles Métier Avancées
- **F11 — Détection de chevauchement** : blocage strict avec code `409` et citation explicite du bail en conflit.
- **F12 — Cohérence temporelle** : date de fin postérieure au début, durée contractuelle minimale d'un mois.
- **F13 — Résiliation anticipée** : clôture anticipée à une date d'effet sans écraser la date de fin initiale (historisée).
- **F14 — Renouvellement de bail** : extension de la date de fin avec vérification d'absence de conflit futur.
- **F15 — Révision de loyer** : révision datée du montant du loyer avec conservation de l'historique dans la table `revision`.
- **F16 — Clôture automatique** : traitement des baux arrivés à terme avec passage au statut `TERMINE` (déclenchable via API ou tâche de fond).

### Niveau 4 — Restitution & Statistiques
- **F17 — Appartements libres** : liste dynamique des biens vacants à une date choisie avec dernier loyer connu.
- **F18 — Reconstitution de l'occupation** : frise chronologique détaillée alternant périodes occupées (avec révisions effectives) et vacances explicites.
- **F19 — Revenu locatif mensuel** : calcul au prorata temporis des jours d'occupation sur un mois `yyyy-MM` et taux d'occupation de l'immeuble.

---

## 🗄️ Base de Données, Migrations Flyway & Vues SQL

La structure de la base de données est gérée automatiquement par **Flyway** au démarrage :

### 1. `V1.0.1__DDL.sql` (Structure & Vues SQL)
- Création de l'ensemble des tables relationnelles (`proprietaire`, `immeuble`, `appartement`, `locataire`, `bail`, `bail_historique`, `revision`, `charge`, `type_constat`, `etat_les_lieux`, `quittance`, `paiement`).
- Définition des index de performance sur les clés étrangères et colonnes de recherche.
- **4 Vues SQL intégrées :**
  - **`v_bail_financier`** : Quittances émises, encaissements, solde débiteur et impayés par bail.
  - **`v_locataire_situation`** : Situation consolidée par locataire (baux totaux/actifs, encaissements, reste dû).
  - **`v_periode_occupation`** : Reconstitution SQL chronologique de l'occupation et de la vacance.
  - **`v_revenu_bail_mensuel`** : Ventilation mensuelle des loyers calculée au prorata temporis des jours.

### 2. `V1.0.2__DML.sql` (Jeu de Données de Test)
- Données référentielles (`type_constat`).
- **JDD complet, réaliste et cohérent** : 20 propriétaires, 15 immeubles, 60 appartements, 40 locataires, 45 baux (terminés, actifs, baux ouverts), révisions de loyers, charges annuelles, quittances et paiements.

---

## 🛠️ Démarrage Local

### Prérequis
- Java 21+
- PostgreSQL local en cours d'exécution

### Configuration (`application.properties`)
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/gestion_locative
spring.datasource.username=postgres
spring.datasource.password=postgres

# Port local par défaut (8080 ou 9090)
server.port=8080
```

### Lancement
```bash
./mvnw clean spring-boot:run
```
L'application exécutera automatiquement les migrations Flyway et sera disponible sur `http://localhost:8080/swagger-ui.html`.
