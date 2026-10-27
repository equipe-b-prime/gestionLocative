#!/bin/bash
# Test Curl — Niveau 2 (N2-F001) — Gestion Locative
# Usage : bash test_n2.sh
# Prérequis : application tourne sur http://localhost:9090

BASE="http://localhost:9090"

echo "╔══════════════════════════════════════════════╗"
echo "║   TEST N2-F001 — GESTION LOCATIVE           ║"
echo "╚══════════════════════════════════════════════╝"
echo ""

# -- Créer un propriétaire de test --
echo "── Création des données de test ──"
PID=$(curl -s -X POST "$BASE/proprietaires" \
  -H "Content-Type: application/json" \
  -d '{"nom":"Test","prenom":"N2","numero":"+224600000099","email":"testn2@test.ci"}' | python3 -c "import sys,json; print(json.load(sys.stdin)['data'][0]['id'])")

IID=$(curl -s -X POST "$BASE/immeubles" \
  -H "Content-Type: application/json" \
  -d "{\"nom\":\"Immeuble N2\",\"adresse\":\"N2 Adresse\",\"nbEtages\":5,\"proprietaireId\":$PID}" | python3 -c "import sys,json; print(json.load(sys.stdin)['data'][0]['id'])")

AID1=$(curl -s -X POST "$BASE/appartements" \
  -H "Content-Type: application/json" \
  -d "{\"nom\":\"Studio N2\",\"surface\":30.0,\"nbPieces\":1,\"etage\":1,\"proprietaireId\":$PID,\"immeubleId\":$IID}" | python3 -c "import sys,json; print(json.load(sys.stdin)['data'][0]['id'])")

AID2=$(curl -s -X POST "$BASE/appartements" \
  -H "Content-Type: application/json" \
  -d "{\"nom\":\"F3 N2\",\"surface\":75.0,\"nbPieces\":3,\"etage\":3,\"proprietaireId\":$PID,\"immeubleId\":$IID}" | python3 -c "import sys,json; print(json.load(sys.stdin)['data'][0]['id'])")

LID=$(curl -s -X POST "$BASE/locataires" \
  -H "Content-Type: application/json" \
  -d '{"nom":"LocN2","prenom":"Test","numero":"+224600000098","email":"locn2@test.ci"}' | python3 -c "import sys,json; print(json.load(sys.stdin)['data'][0]['id'])")

curl -s -X POST "$BASE/baux" \
  -H "Content-Type: application/json" \
  -d "{\"dateDebut\":\"2026-06-01T00:00:00.000Z\",\"dateFin\":\"2027-05-31T00:00:00.000Z\",\"montantLoyer\":350000,\"appartementId\":$AID1,\"locataireId\":$LID}" > /dev/null

echo "  PID=$PID IID=$IID AID1=$AID1 AID2=$AID2 LID=$LID"
echo ""

# -- F6 --
echo "── F6 — Appartements d'un immeuble ──"
R=$(curl -s "$BASE/immeubles/$IID/appartements")
echo "$R" | python3 -c "
import sys,json; d=json.load(sys.stdin)
print(f'  {len(d[\"data\"])} appartements')
for a in d['data']: print(f'    {a[\"nom\"]} - étage {a[\"etage\"]}, {a[\"surface\"]}m², occupé={a[\"etageOccupe\"]}')
" && echo "  ✅ F6 OK" || echo "  ❌ F6 ÉCHEC"

# -- F7 --
echo ""
echo "── F7 — Immeubles d'un propriétaire ──"
R=$(curl -s "$BASE/proprietaires/$PID/immeubles")
echo "$R" | python3 -c "
import sys,json; d=json.load(sys.stdin)
print(f'  {len(d[\"data\"])} immeubles pour ce propriétaire')
for i in d['data']: print(f'    {i[\"nom\"]} - {i[\"nbAppartements\"]} app, {i[\"nbBauxEnCours\"]} baux en cours')
" && echo "  ✅ F7 OK" || echo "  ❌ F7 ÉCHEC"

# -- F8 --
echo ""
echo "── F8 — Baux d'un appartement ──"
R=$(curl -s "$BASE/appartements/$AID1/baux")
echo "$R" | python3 -c "
import sys,json; d=json.load(sys.stdin)
print(f'  {len(d[\"data\"])} baux pour cet appartement')
for b in d['data']: print(f'    Bail #{b[\"id\"]}: enCours={b[\"enCours\"]}')
" && echo "  ✅ F8 OK" || echo "  ❌ F8 ÉCHEC"

# -- F9 --
echo ""
echo "── F9 — Recherche appartements ──"
R=$(curl -s "$BASE/appartements?pieces=3")
echo "$R" | python3 -c "
import sys,json; d=json.load(sys.stdin)
print(f'  {len(d[\"data\"])} appartements avec 3 pièces')
" && echo "  ✅ F9 (pieces=3) OK" || echo "  ❌ F9 ÉCHEC"

R=$(curl -s "$BASE/appartements?surfaceMin=50")
echo "$R" | python3 -c "
import sys,json; d=json.load(sys.stdin)
print(f'  {len(d[\"data\"])} appartements surface>=50')
" && echo "  ✅ F9 (surfaceMin=50) OK" || echo "  ❌ F9 ÉCHEC"

R=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/appartements?surfaceMin=100&surfaceMax=50")
echo "$R" | grep -q "400" && echo "  ✅ F9 (surfaceMin>surfaceMax → 400) OK" || echo "  ❌ F9 ÉCHEC"

# -- F10 --
echo ""
echo "── F10 — Pagination baux avec tri ──"
R=$(curl -s "$BASE/baux?page=0&size=20&sort=dateDebut,desc")
echo "$R" | python3 -c "
import sys,json; d=json.load(sys.stdin)
print(f'  {d[\"nombreTotalElements\"]} baux, {d.get(\"totalPages\",\"?\")} pages')
" && echo "  ✅ F10 OK" || echo "  ❌ F10 ÉCHEC"

echo ""
echo "╔══════════════════════════════════════════════╗"
echo "║   ✅ TESTS NIVEAU 2 TERMINÉS               ║"
echo "╚══════════════════════════════════════════════╝"