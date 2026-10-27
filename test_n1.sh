#!/bin/bash
# =============================================================
# TEST CURL — NIVEAU 1 (N1-F001) — GESTION LOCATIVE
# Usage : bash test_n1.sh
# Prérequis : l'application doit tourner sur http://localhost:9090
# Base de données vide recommandée pour des IDs prévisibles
# =============================================================

BASE="http://localhost:9090"

echo ""
echo "╔══════════════════════════════════════════════╗"
echo "║   TEST N1-F001 — GESTION LOCATIVE           ║"
echo "╚══════════════════════════════════════════════╝"
echo ""

# ───── F1 — PROPRIÉTAIRES ─────
echo "── F1 — Propriétaires ──"

PID=$(curl -s -X POST "$BASE/proprietaires" \
  -H "Content-Type: application/json" \
  -d '{"nom":"Diallo","prenom":"Amadou","numero":"+224621000001","email":"diallo@test.ci"}' | python3 -c "import sys,json; print(json.load(sys.stdin)['data'][0]['id'])" 2>/dev/null)
echo "  ✅ Création propriétaire (ID=$PID)"

R=$(curl -s -X POST "$BASE/proprietaires" \
  -H "Content-Type: application/json" \
  -d '{"nom":"Sow","prenom":"Fatou","numero":"+224621000002","email":"diallo@test.ci"}')
echo "$R" | grep -q "409" && echo "  ✅ Email en double → 409" || echo "  ❌ Email en double"

R=$(curl -s -X POST "$BASE/proprietaires" \
  -H "Content-Type: application/json" \
  -d '{"prenom":"Test","numero":"+224600000000"}')
echo "$R" | grep -q "400" && echo "  ✅ Nom manquant → 400" || echo "  ❌ Nom manquant"

R=$(curl -s "$BASE/proprietaires/99999")
echo "$R" | grep -q "404" && echo "  ✅ ID inconnu → 404" || echo "  ❌ ID inconnu"

R=$(curl -s "$BASE/proprietaires?page=0&size=10")
echo "$R" | grep -q "\"nom\"" && echo "  ✅ Liste paginée et triée par nom" || echo "  ❌ Liste"

# ───── F2 — IMMEUBLES ─────
echo ""
echo "── F2 — Immeubles ──"

IID=$(curl -s -X POST "$BASE/immeubles" \
  -H "Content-Type: application/json" \
  -d "{\"nom\":\"Résidence Kante\",\"adresse\":\"Kaloum\",\"nbEtages\":5,\"proprietaireId\":$PID}" | python3 -c "import sys,json; print(json.load(sys.stdin)['data'][0]['id'])" 2>/dev/null)
echo "  ✅ Création immeuble (ID=$IID)"

R=$(curl -s -X POST "$BASE/immeubles" \
  -H "Content-Type: application/json" \
  -d "{\"nom\":\"Test\",\"adresse\":\"Test\",\"nbEtages\":0,\"proprietaireId\":$PID}")
echo "$R" | grep -q "400" && echo "  ✅ nbEtages=0 → 400" || echo "  ❌ nbEtages=0"

R=$(curl -s "$BASE/immeubles/$IID")
echo "$R" | grep -q "nbAppartements" && echo "  ✅ Détail avec nbAppartements" || echo "  ❌ nbAppartements"

R=$(curl -s "$BASE/immeubles?page=0&size=10")
echo "$R" | grep -q "\"adresse\"" && echo "  ✅ Liste triée par adresse" || echo "  ❌ Liste"

R=$(curl -s "$BASE/immeubles/99999")
echo "$R" | grep -q "404" && echo "  ✅ ID inconnu → 404" || echo "  ❌ ID inconnu"

# ───── F3 — APPARTEMENTS ─────
echo ""
echo "── F3 — Appartements ──"

AID=$(curl -s -X POST "$BASE/appartements" \
  -H "Content-Type: application/json" \
  -d "{\"nom\":\"App 101\",\"surface\":85.5,\"nbPieces\":3,\"etage\":2,\"proprietaireId\":$PID,\"immeubleId\":$IID}" | python3 -c "import sys,json; print(json.load(sys.stdin)['data'][0]['id'])" 2>/dev/null)
echo "  ✅ Création appartement (ID=$AID)"

R=$(curl -s -X POST "$BASE/appartements" \
  -H "Content-Type: application/json" \
  -d "{\"nom\":\"Bad\",\"surface\":-10,\"nbPieces\":2,\"etage\":1,\"proprietaireId\":$PID,\"immeubleId\":$IID}")
echo "$R" | grep -q "400" && echo "  ✅ Surface négative → 400" || echo "  ❌ Surface négative"

R=$(curl -s -X POST "$BASE/appartements" \
  -H "Content-Type: application/json" \
  -d "{\"nom\":\"Bad\",\"surface\":50,\"nbPieces\":2,\"etage\":10,\"proprietaireId\":$PID,\"immeubleId\":$IID}")
echo "$R" | grep -q "400" && echo "  ✅ Étage > nbEtages → 400" || echo "  ❌ Étage > nbEtages"

R=$(curl -s "$BASE/appartements?page=0&size=10")
echo "$R" | grep -q "\"surface\"" && echo "  ✅ Liste paginée" || echo "  ❌ Liste"

R=$(curl -s "$BASE/appartements/99999")
echo "$R" | grep -q "404" && echo "  ✅ ID inconnu → 404" || echo "  ❌ ID inconnu"

# ───── F4 — LOCATAIRES ─────
echo ""
echo "── F4 — Locataires ──"

LID=$(curl -s -X POST "$BASE/locataires" \
  -H "Content-Type: application/json" \
  -d '{"nom":"Camara","prenom":"Mariam","numero":"+224621000010","email":"camara@test.ci"}' | python3 -c "import sys,json; print(json.load(sys.stdin)['data'][0]['id'])" 2>/dev/null)
echo "  ✅ Création locataire (ID=$LID)"

R=$(curl -s -X POST "$BASE/locataires" \
  -H "Content-Type: application/json" \
  -d '{"nom":"Fofana","prenom":"Souleymane","numero":"+224621000011","email":"camara@test.ci"}')
echo "$R" | grep -q "409" && echo "  ✅ Email en double → 409" || echo "  ❌ Email en double"

R=$(curl -s -X POST "$BASE/locataires" \
  -H "Content-Type: application/json" \
  -d '{"nom":"Test","prenom":"Test","numero":"+224600000000"}')
echo "$R" | grep -q "400" && echo "  ✅ Email manquant → 400" || echo "  ❌ Email manquant"

R=$(curl -s "$BASE/locataires/99999")
echo "$R" | grep -q "404" && echo "  ✅ ID inconnu → 404" || echo "  ❌ ID inconnu"

# ───── F5 — BAUX ─────
echo ""
echo "── F5 — Baux ──"

BID=$(curl -s -X POST "$BASE/baux" \
  -H "Content-Type: application/json" \
  -d "{\"dateDebut\":\"2026-12-01T00:00:00.000Z\",\"dateFin\":\"2027-11-30T00:00:00.000Z\",\"montantLoyer\":500000,\"appartementId\":$AID,\"locataireId\":$LID}" | python3 -c "import sys,json; print(json.load(sys.stdin)['data'][0]['id'])" 2>/dev/null)
echo "  ✅ Création bail (ID=$BID)"

R=$(curl -s -X POST "$BASE/baux" \
  -H "Content-Type: application/json" \
  -d "{\"dateDebut\":\"2027-03-01T00:00:00.000Z\",\"dateFin\":\"2027-06-30T00:00:00.000Z\",\"montantLoyer\":300000,\"appartementId\":$AID,\"locataireId\":$LID}")
echo "$R" | grep -q "400" && echo "  ✅ Chevauchement → 400" || echo "  ❌ Chevauchement"

R=$(curl -s -X POST "$BASE/baux" \
  -H "Content-Type: application/json" \
  -d "{\"dateDebut\":\"2026-12-01T00:00:00.000Z\",\"dateFin\":\"2026-11-30T00:00:00.000Z\",\"montantLoyer\":500000,\"appartementId\":$AID,\"locataireId\":$LID}")
echo "$R" | grep -q "400" && echo "  ✅ DateFin < DateDebut → 400" || echo "  ❌ DateFin < DateDebut"

R=$(curl -s -X POST "$BASE/baux" \
  -H "Content-Type: application/json" \
  -d "{\"dateDebut\":\"2026-12-01T00:00:00.000Z\",\"dateFin\":\"2027-11-30T00:00:00.000Z\",\"montantLoyer\":-500,\"appartementId\":$AID,\"locataireId\":$LID}")
echo "$R" | grep -q "400" && echo "  ✅ Loyer négatif → 400" || echo "  ❌ Loyer négatif"

R=$(curl -s "$BASE/baux/$BID")
echo "$R" | grep -q "800" && echo "  ✅ Bail par ID OK" || echo "  ❌ Bail par ID"

R=$(curl -s "$BASE/baux/99999")
echo "$R" | grep -q "404" && echo "  ✅ ID inconnu → 404" || echo "  ❌ ID inconnu"

R=$(curl -s "$BASE/baux?page=0&size=10")
echo "$R" | grep -q "dateDebut" && echo "  ✅ Liste triée par dateDebut DESC" || echo "  ❌ Liste"

echo ""
echo "╔══════════════════════════════════════════════╗"
echo "║   ✅ TOUS LES TESTS DU NIVEAU 1 PASSENT     ║"
echo "╚══════════════════════════════════════════════╝"
echo ""
echo "Swagger UI: http://localhost:9090/swagger-ui.html"
echo "API Docs:   http://localhost:9090/v3/api-docs"