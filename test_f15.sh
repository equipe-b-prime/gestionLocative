#!/bin/bash
# Test F15 - révision du loyer. Usage: ./test_f15.sh [BASE_URL]
BASE=${1:-http://localhost:8181}
H='Content-Type: application/json'
S=$(date +%s)
id() { python3 -c "import sys,json; print(json.load(sys.stdin)['data'][0]['id'])"; }

PID=$(curl -s -X POST "$BASE/proprietaires" -H "$H" -d "{\"nom\":\"P$S\",\"prenom\":\"F15\",\"numero\":\"+2246$S\",\"email\":\"p$S@t.ci\"}" | id)
IID=$(curl -s -X POST "$BASE/immeubles" -H "$H" -d "{\"nom\":\"Imm$S\",\"adresse\":\"Adr\",\"nbEtages\":5,\"proprietaireId\":$PID}" | id)
AID=$(curl -s -X POST "$BASE/appartements" -H "$H" -d "{\"nom\":\"Apt$S\",\"surface\":40.0,\"nbPieces\":2,\"etage\":1,\"proprietaireId\":$PID,\"immeubleId\":$IID}" | id)
LID=$(curl -s -X POST "$BASE/locataires" -H "$H" -d "{\"nom\":\"L$S\",\"prenom\":\"F15\",\"numero\":\"+2247$S\",\"email\":\"l$S@t.ci\"}" | id)
BID=$(curl -s -X POST "$BASE/baux" -H "$H" -d "{\"dateDebut\":\"2027-01-01T00:00:00.000Z\",\"dateFin\":\"2032-12-31T00:00:00.000Z\",\"montantLoyer\":300000,\"appartementId\":$AID,\"locataireId\":$LID}" | id)
echo "bail=$BID"

rev() { curl -s -w " [HTTP %{http_code}]\n" -X POST "$BASE/baux/$BID/revision" -H "$H" -d "$1"; }
echo "1. revision valide 2028-01-01 -> 320000";  rev '{"nouveauMontant":320000,"dateEffet":"2028-01-01"}'
echo "2. meme date (doit echouer)";             rev '{"nouveauMontant":330000,"dateEffet":"2028-01-01"}'
echo "3. date anterieure (doit echouer)";       rev '{"nouveauMontant":330000,"dateEffet":"2027-06-01"}'
echo "4. avant debut bail (doit echouer)";      rev '{"nouveauMontant":330000,"dateEffet":"2026-06-01"}'
echo "5. sans dateEffet (doit echouer)";        rev '{"nouveauMontant":330000}'
echo "6. montant negatif (doit echouer)";       rev '{"nouveauMontant":-5,"dateEffet":"2029-01-01"}'
echo "7. revision future 2099 hors bail (doit echouer)"; rev '{"nouveauMontant":400000,"dateEffet":"2099-01-01"}'
echo "8. revision 2029-01-01 -> 340000";        rev '{"nouveauMontant":340000,"dateEffet":"2029-01-01"}'
echo "9. loyer courant du bail (revisions futures => reste 300000)"; curl -s "$BASE/baux/$BID" | python3 -c "import sys,json; d=json.load(sys.stdin)[\"data\"][0]; print(d[\"montantLoyer\"])"
