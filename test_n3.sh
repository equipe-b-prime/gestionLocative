#!/bin/bash
# Niveau 3 - règles métier F11 à F16. Usage: ./test_n3.sh [BASE_URL]
BASE=${1:-http://localhost:8181}
H='Content-Type: application/json'
S=$(date +%s)$RANDOM
PASS=0; FAIL=0
PSQL="env PGPASSWORD=postgres psql -h localhost -U postgres -d gestion_locative -tA"

d() { python3 -W ignore -c "import datetime,sys; print((datetime.datetime.utcnow()+datetime.timedelta(days=int(sys.argv[1]),seconds=int(sys.argv[2]))).strftime('%Y-%m-%dT%H:%M:%S.000Z'))" "$1" "${2:-0}"; }
day() { python3 -W ignore -c "import datetime,sys; print((datetime.date.today()+datetime.timedelta(days=int(sys.argv[1]))).isoformat())" "$1"; }
jq_() { python3 -c "import sys,json; d=json.load(sys.stdin); print(eval(sys.argv[1]))" "$1"; }
id() { jq_ "d['data'][0]['id']"; }

# req METHOD URL BODY -> sets CODE and BODY_OUT
req() { local out; out=$(curl -s -w "\n%{http_code}" -X "$1" "$BASE$2" -H "$H" ${3:+-d "$3"}); CODE=${out##*$'\n'}; BODY_OUT=${out%$'\n'*}; }
check() { # desc expected_code [substring]
  if [ "$CODE" = "$2" ] && { [ -z "$3" ] || [[ "$BODY_OUT" == *"$3"* ]]; }; then PASS=$((PASS+1)); echo "  ✅ $1 -> $CODE"
  else FAIL=$((FAIL+1)); echo "  ❌ $1 -> attendu $2${3:+ avec '$3'}, obtenu $CODE : ${BODY_OUT:0:200}"; fi
}
mkbail() { # apt dateDebut dateFin(or "") -> id
  local fin=""; [ -n "$3" ] && fin=",\"dateFin\":\"$3\""
  curl -s -X POST "$BASE/baux" -H "$H" -d "{\"dateDebut\":\"$2\"$fin,\"montantLoyer\":300000,\"appartementId\":$1,\"locataireId\":$LID}" | id
}
mkapt() { curl -s -X POST "$BASE/appartements" -H "$H" -d "{\"nom\":\"$1$S\",\"surface\":40.0,\"nbPieces\":2,\"etage\":1,\"proprietaireId\":$PID,\"immeubleId\":$IID}" | id; }

echo "── Données de test ──"
PID=$(curl -s -X POST "$BASE/proprietaires" -H "$H" -d "{\"nom\":\"P$S\",\"prenom\":\"N3\",\"numero\":\"+2246$S\",\"email\":\"p$S@t.ci\"}" | id)
IID=$(curl -s -X POST "$BASE/immeubles" -H "$H" -d "{\"nom\":\"Imm$S\",\"adresse\":\"Adr\",\"nbEtages\":5,\"proprietaireId\":$PID}" | id)
LID=$(curl -s -X POST "$BASE/locataires" -H "$H" -d "{\"nom\":\"L$S\",\"prenom\":\"N3\",\"numero\":\"+2247$S\",\"email\":\"l$S@t.ci\"}" | id)
A1=$(mkapt A); A2=$(mkapt B); A3=$(mkapt C); A4=$(mkapt D); A5=$(mkapt E)
echo "  proprietaire=$PID immeuble=$IID locataire=$LID appartements=$A1,$A2,$A3,$A4,$A5"

echo "── F12 - cohérence des dates ──"
req POST /baux "{\"dateDebut\":\"$(d 5)\",\"dateFin\":\"$(d 4)\",\"montantLoyer\":1,\"appartementId\":$A1,\"locataireId\":$LID}"
check "fin avant début" 400 "postérieure"
req POST /baux "{\"dateDebut\":\"$(d 5)\",\"dateFin\":\"$(d 20)\",\"montantLoyer\":1,\"appartementId\":$A1,\"locataireId\":$LID}"
check "durée < 1 mois" 400 "un mois"

echo "── F11 - chevauchement (409) ──"
B1=$(mkbail $A1 "$(d 0 3)" "$(d 180)")
echo "  bail de référence #$B1 sur apt $A1"
req POST /baux "{\"dateDebut\":\"$(d 60)\",\"dateFin\":\"$(d 120)\",\"montantLoyer\":1,\"appartementId\":$A1,\"locataireId\":$LID}"
check "période incluse dans un bail existant" 409 "bail #$B1"
req POST /baux "{\"dateDebut\":\"$(d 100)\",\"montantLoyer\":1,\"appartementId\":$A1,\"locataireId\":$LID}"
check "bail sans fin qui recouvre" 409 "bail #$B1"
req POST /baux "{\"dateDebut\":\"$(d 200)\",\"dateFin\":\"$(d 300)\",\"montantLoyer\":1,\"appartementId\":$A1,\"locataireId\":$LID}"
check "bail après la fin du précédent" 200
BS=$(mkbail $A2 "$(d 10)" "")
echo "  bail sans fin #$BS sur apt $A2"
req POST /baux "{\"dateDebut\":\"$(d 400)\",\"dateFin\":\"$(d 500)\",\"montantLoyer\":1,\"appartementId\":$A2,\"locataireId\":$LID}"
check "bail sans fin bloque toute période postérieure" 409 "sans date de fin"
req PUT /baux/$B1 "{\"dateDebut\":\"$(d 0 3)\",\"dateFin\":\"$(d 10)\",\"montantLoyer\":1,\"appartementId\":$A1,\"locataireId\":$LID}"
check "F12 PUT durée < 1 mois" 400 "un mois"

echo "── F13 - résiliation ──"
BF=$(mkbail $A3 "$(d 30)" "$(d 90)")
req POST /baux/$BF/resiliation "{\"dateEffet\":\"$(day 40)\"}"
check "bail pas commencé" 409 "pas encore commencé"
sleep 4   # B1 a démarré (début = maintenant + 3 s)
req POST /baux/$B1/resiliation "{\"dateEffet\":\"$(day -1)\"}"
check "date antérieure à aujourd'hui" 400 "aujourd'hui"
req POST /baux/$B1/resiliation "{\"dateEffet\":\"$(day 400)\"}"
check "date hors période" 400 "période"
req POST /baux/$B1/resiliation "{\"dateEffet\":\"$(day 20)\"}"
check "résiliation valide" 200 "RESILIE"
req POST /baux/$B1/resiliation "{\"dateEffet\":\"$(day 21)\"}"
check "déjà résilié" 409 "déjà résilié"
req POST /baux "{\"dateDebut\":\"$(d 22)\",\"dateFin\":\"$(d 100)\",\"montantLoyer\":1,\"appartementId\":$A1,\"locataireId\":$LID}"
check "appartement relouable après la date d'effet" 200

echo "── F14 - renouvellement ──"
BR=$(mkbail $A4 "$(d 0 3)" "$(d 40)")
BN=$(mkbail $A4 "$(d 70)" "$(d 130)")
sleep 4
req POST /baux/$BR/renouvellement "{\"nouvelleDateFin\":\"$(day 30)\"}"
check "nouvelle date <= date de fin actuelle" 400 "postérieure"
req POST /baux/$BR/renouvellement "{\"nouvelleDateFin\":\"$(day 80)\"}"
check "période ajoutée chevauche un autre bail" 409 "bail #$BN"
req POST /baux/$BR/renouvellement "{\"nouvelleDateFin\":\"$(day 60)\"}"
check "renouvellement valide" 200
echo "  historique: $($PSQL -c "select type||' ancienne='||ancienne_date_fin::date||' nouvelle='||nouvelle_date_fin::date from bail_historique where bail_id=$BR")"
req POST /baux/$B1/renouvellement "{\"nouvelleDateFin\":\"$(day 500)\"}"
check "bail résilié non renouvelable" 409 "résilié"

echo "── F15 - révision ──"
BV=$(mkbail $A5 "$(d 30)" "$(d 400)")
req POST /baux/$BV/revision "{\"nouveauMontant\":320000,\"dateEffet\":\"$(day 100)\"}"
check "révision valide" 200
req POST /baux/$BV/revision "{\"nouveauMontant\":330000,\"dateEffet\":\"$(day 100)\"}"
check "révision déjà existante à cette date" 409 "existe déjà"
req POST /baux/$BV/revision "{\"nouveauMontant\":0,\"dateEffet\":\"$(day 200)\"}"
check "montant non positif" 400
req POST /baux/$BV/revision "{\"nouveauMontant\":330000,\"dateEffet\":\"$(day 50)\"}"
check "date antérieure à la dernière révision" 400

echo "── F16 - clôture des baux arrivés à terme ──"
BT=$(mkbail $(mkapt F) "$(d 0 3)" "$(d 40)")
sleep 4
$PSQL -c "update bail set date_fin = now() - interval '2 days' where id=$BT" >/dev/null
req POST /baux/cloture
check "déclenchement de la clôture" 200 "bauxClotures"
req GET /baux/$BT
check "bail passé en TERMINE" 200 "TERMINE"
[[ "$BODY_OUT" == *'"enCours":false'* ]] && { PASS=$((PASS+1)); echo "  ✅ plus compté comme en cours"; } || { FAIL=$((FAIL+1)); echo "  ❌ encore en cours"; }
req POST /baux/cloture
check "idempotent (rien à clôturer)" 200 '"bauxClotures":0'

echo; echo "Résultat : $PASS réussis, $FAIL échecs"
[ $FAIL -eq 0 ]
