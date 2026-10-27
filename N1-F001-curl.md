# N1-F001 — Tests Curl Positifs — Gestion Locative

> **Branche :** `N1-F001` (commit `dd4f04d`)  
> **Application :** `http://localhost:9090`  
> **Testé avec :** `bash test_n1.sh` (IDs dynamiques)

---

## F1 — Propriétaires

```bash
# Création propriétaire
curl -s -X POST http://localhost:9090/proprietaires \
  -H "Content-Type: application/json" \
  -d '{"nom":"Diallo","prenom":"Amadou","numero":"+224621000001","email":"diallo@test.ci"}'

# Email en double → 409
curl -s -X POST http://localhost:9090/proprietaires \
  -H "Content-Type: application/json" \
  -d '{"nom":"Sow","prenom":"Fatou","numero":"+224621000002","email":"diallo@test.ci"}'

# Nom manquant → 400
curl -s -X POST http://localhost:9090/proprietaires \
  -H "Content-Type: application/json" \
  -d '{"prenom":"Test","numero":"+224600000000"}'

# ID inconnu → 404
curl -s http://localhost:9090/proprietaires/99999

# Liste paginée et triée par nom
curl -s "http://localhost:9090/proprietaires?page=0&size=10"
```

---

## F2 — Immeubles

```bash
# Création immeuble
curl -s -X POST http://localhost:9090/immeubles \
  -H "Content-Type: application/json" \
  -d '{"nom":"Résidence Kante","adresse":"Kaloum","nbEtages":5,"proprietaireId":1}'

# nbEtages=0 → 400
curl -s -X POST http://localhost:9090/immeubles \
  -H "Content-Type: application/json" \
  -d '{"nom":"Test","adresse":"Test","nbEtages":0,"proprietaireId":1}'

# Détail avec nbAppartements
curl -s http://localhost:9090/immeubles/1

# Liste triée par adresse
curl -s "http://localhost:9090/immeubles?page=0&size=10"

# ID inconnu → 404
curl -s http://localhost:9090/immeubles/99999
```

---

## F3 — Appartements

```bash
# Création appartement
curl -s -X POST http://localhost:9090/appartements \
  -H "Content-Type: application/json" \
  -d '{"nom":"App 101","surface":85.5,"nbPieces":3,"etage":2,"proprietaireId":1,"immeubleId":1}'

# Surface négative → 400
curl -s -X POST http://localhost:9090/appartements \
  -H "Content-Type: application/json" \
  -d '{"nom":"Bad","surface":-10,"nbPieces":2,"etage":1,"proprietaireId":1,"immeubleId":1}'

# Étage > nbEtages immeuble → 400
curl -s -X POST http://localhost:9090/appartements \
  -H "Content-Type: application/json" \
  -d '{"nom":"Bad","surface":50,"nbPieces":2,"etage":10,"proprietaireId":1,"immeubleId":1}'

# Liste paginée
curl -s "http://localhost:9090/appartements?page=0&size=10"

# ID inconnu → 404
curl -s http://localhost:9090/appartements/99999
```

---

## F4 — Locataires

```bash
# Création locataire
curl -s -X POST http://localhost:9090/locataires \
  -H "Content-Type: application/json" \
  -d '{"nom":"Camara","prenom":"Mariam","numero":"+224621000010","email":"camara@test.ci"}'

# Email en double → 409
curl -s -X POST http://localhost:9090/locataires \
  -H "Content-Type: application/json" \
  -d '{"nom":"Fofana","prenom":"Souleymane","numero":"+224621000011","email":"camara@test.ci"}'

# Email manquant → 400
curl -s -X POST http://localhost:9090/locataires \
  -H "Content-Type: application/json" \
  -d '{"nom":"Test","prenom":"Test","numero":"+224600000000"}'

# ID inconnu → 404
curl -s http://localhost:9090/locataires/99999

# Liste triée par nom
curl -s "http://localhost:9090/locataires?page=0&size=10"
```

---

## F5 — Baux

```bash
# Création bail (ID=$BID)
curl -s -X POST http://localhost:9090/baux \
  -H "Content-Type: application/json" \
  -d '{"dateDebut":"2026-12-01T00:00:00.000Z","dateFin":"2027-11-30T00:00:00.000Z","montantLoyer":500000,"appartementId":1,"locataireId":1}'

# Chevauchement → 400
curl -s -X POST http://localhost:9090/baux \
  -H "Content-Type: application/json" \
  -d '{"dateDebut":"2027-03-01T00:00:00.000Z","dateFin":"2027-06-30T00:00:00.000Z","montantLoyer":300000,"appartementId":1,"locataireId":1}'

# DateFin < DateDebut → 400
curl -s -X POST http://localhost:9090/baux \
  -H "Content-Type: application/json" \
  -d '{"dateDebut":"2026-12-01T00:00:00.000Z","dateFin":"2026-11-30T00:00:00.000Z","montantLoyer":500000,"appartementId":1,"locataireId":1}'

# Loyer négatif → 400
curl -s -X POST http://localhost:9090/baux \
  -H "Content-Type: application/json" \
  -d '{"dateDebut":"2026-12-01T00:00:00.000Z","dateFin":"2027-11-30T00:00:00.000Z","montantLoyer":-500,"appartementId":1,"locataireId":1}'

# Bail par ID
curl -s http://localhost:9090/baux/1

# ID inconnu → 404
curl -s http://localhost:9090/baux/99999

# Liste triée par dateDebut DESC
curl -s "http://localhost:9090/baux?page=0&size=10"
```

---

## 📦 Commandes d'exécution

```bash
# Lancer l'application
cd /home/yokan/TAF/FORMATION/TESTO/gestionLocative
./mvnw spring-boot:run

# Lancer les tests
bash test_n1.sh

# Vérifier le statut Git
git status
git log --oneline -5
```

---

## 📂 Fichier créé

- `test_n1.sh` — Script de test curl N1-F001 (IDs dynamiques)  
  Commit : `dd4f04d` | Branche : `N1-F001`