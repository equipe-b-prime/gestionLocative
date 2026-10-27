# N2-F001 — Tests Curl Positifs — Niveau 2

> **Branche :** `N2-F001` (commit `3540e37`)  
> **Application :** `http://localhost:9090`

---

## F6 — Lister les appartements d'un immeuble

```bash
# Appartements d'un immeuble (avec surface et état d'occupation)
curl -s "http://localhost:9090/immeubles/1/appartements" | python3 -m json.tool

# Immeuble sans appartement → liste vide
curl -s "http://localhost:9090/immeubles/999/appartements" | python3 -m json.tool

# Immeuble inexistant → 404
curl -s -o /dev/null -w "HTTP %{http_code}" "http://localhost:9090/immeubles/99999/appartements"
echo ""
```

---

## F7 — Lister les immeubles d'un propriétaire

```bash
# Immeubles d'un propriétaire
curl -s "http://localhost:9090/proprietaires/1/immeubles" | python3 -m json.tool

# Propriétaire sans immeuble → liste vide
curl -s "http://localhost:9090/proprietaires/999/immeubles" | python3 -m json.tool

# Propriétaire inexistant → 404
curl -s -o /dev/null -w "HTTP %{http_code}" "http://localhost:9090/proprietaires/99999/immeubles"
echo ""

```

---

## F8 — Consulter l'historique des baux d'un appartement

```bash
# Baux d'un appartement (triés du plus récent au plus ancien)
curl -s "http://localhost:9090/appartements/1/baux" | python3 -m json.tool

# Appartement jamais loué → liste vide
curl -s "http://localhost:9090/appartements/999/baux" | python3 -m json.tool

# Appartement inexistant → 404
curl -s -o /dev/null -w "HTTP %{http_code}" "http://localhost:9090/appartements/99999/baux"
echo ""

```

---

## F9 — Rechercher des appartements par critères

```bash
# Recherche par nombre de pièces
curl -s "http://localhost:9090/appartements?pieces=3" | python3 -m json.tool

# Recherche par surface minimale
curl -s "http://localhost:9090/appartements?surfaceMin=70" | python3 -m json.tool

# Recherche par surface maximale
curl -s "http://localhost:9090/appartements?surfaceMax=30" | python3 -m json.tool

# Combinaison de critères
curl -s "http://localhost:9090/appartements?surfaceMin=50&surfaceMax=100&pieces=3" | python3 -m json.tool

# Surface min > max → 400
curl -s -o /dev/null -w "HTTP %{http_code}" "http://localhost:9090/appartements?surfaceMin=100&surfaceMax=50"
echo ""

# Sans critères → tous les appartements
curl -s "http://localhost:9090/appartements?page=0&size=10" | python3 -m json.tool

```

---

## F10 — Paginer la liste des baux

```bash
# Page 0 avec 20 éléments, tri par date de début décroissante
curl -s "http://localhost:9090/baux?page=0&size=20&sort=dateDebut,desc" | python3 -m json.tool

# Sans paramètres (valeurs par défaut)
curl -s "http://localhost:9090/baux" | python3 -m json.tool

# Tri ascendant
curl -s "http://localhost:9090/baux?sort=dateDebut,asc" | python3 -m json.tool

# Taille max (100)
curl -s "http://localhost:9090/baux?size=200" | python3 -c "import sys,json; d=json.load(sys.stdin); print(f'Size: {d[\"nombreTotalElements\"]}, Total: {d[\"totalElements\"]}')"

```

---

## 📦 Résumé

| Feature | Endpoint | Statut |
|---|---|---|
| **F6** | `GET /immeubles/{id}/appartements` | ✅ |
| **F7** | `GET /proprietaires/{id}/immeubles` | ✅ |
| **F8** | `GET /appartements/{id}/baux` | ✅ |
| **F9** | `GET /appartements?surfaceMin=&surfaceMax=&pieces=` | ✅ |
| **F10** | `GET /baux?page=&size=&sort=` | ✅ |