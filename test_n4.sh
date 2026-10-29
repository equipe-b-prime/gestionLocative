#!/bin/bash
# Niveau 4 (F17, F18, F19) + réserves du niveau 3 (baux déjà commencés). Usage: ./test_n4.sh [BASE_URL]
BASE=${1:-http://localhost:8181}
H='Content-Type: application/json'
S=$(date +%s)$RANDOM
PASS=0; FAIL=0

jq_() { python3 -W ignore -c "import sys,json; d=json.load(sys.stdin); print(eval(sys.argv[1]))" "$1"; }
id() { jq_ "d['data'][0]['id']"; }
req() { local out; out=$(curl -s -w "\n%{http_code}" -X "$1" "$BASE$2" -H "$H" ${3:+-d "$3"}); CODE=${out##*$'\n'}; BODY_OUT=${out%$'\n'*}; }
# ok DESC EXPR [CODE]  -> evalue EXPR (python, variable d = JSON) sur BODY_OUT, et verifie le code HTTP
ok() {
  local want=${3:-200} val
  val=$(echo "$BODY_OUT" | jq_ "$2" 2>/dev/null)
  if [ "$CODE" = "$want" ] && [ "$val" = "True" ]; then PASS=$((PASS+1)); echo "  ✅ $1"
  else FAIL=$((FAIL+1)); echo "  ❌ $1 -> code $CODE (attendu $want), expr=[$val] : ${BODY_OUT:0:300}"; fi
}
code() { if [ "$CODE" = "$2" ]; then PASS=$((PASS+1)); echo "  ✅ $1 -> $CODE"; else FAIL=$((FAIL+1)); echo "  ❌ $1 -> attendu $2, obtenu $CODE : ${BODY_OUT:0:200}"; fi; }
mkapt() { curl -s -X POST "$BASE/appartements" -H "$H" -d "{\"nom\":\"$1$S\",\"surface\":$2,\"nbPieces\":2,\"etage\":$3,\"proprietaireId\":$PID,\"immeubleId\":$4}" | id; }
mkbail() { local fin=""; [ -n "$3" ] && fin=",\"dateFin\":\"$3\""
  curl -s -X POST "$BASE/baux" -H "$H" -d "{\"dateDebut\":\"$2\"$fin,\"montantLoyer\":$4,\"appartementId\":$1,\"locataireId\":$LID}" | id; }

echo "── Données de test ──"
PID=$(curl -s -X POST "$BASE/proprietaires" -H "$H" -d "{\"nom\":\"P$S\",\"prenom\":\"N4\",\"numero\":\"+2246$S\",\"email\":\"p$S@t.ci\"}" | id)
IID=$(curl -s -X POST "$BASE/immeubles" -H "$H" -d "{\"nom\":\"Imm4-$S\",\"adresse\":\"Adr4-$S\",\"nbEtages\":5,\"proprietaireId\":$PID}" | id)
IVID=$(curl -s -X POST "$BASE/immeubles" -H "$H" -d "{\"nom\":\"ImmVide-$S\",\"adresse\":\"AdrVide-$S\",\"nbEtages\":5,\"proprietaireId\":$PID}" | id)
LID=$(curl -s -X POST "$BASE/locataires" -H "$H" -d "{\"nom\":\"L$S\",\"prenom\":\"N4\",\"numero\":\"+2247$S\",\"email\":\"l$S@t.ci\"}" | id)
A=$(mkapt A 50.0 1 $IID); B=$(mkapt B 30.0 2 $IID); C=$(mkapt C 70.0 3 $IID); V=$(mkapt V 40.0 1 $IVID)
# A : 300000 du 2026-01-01 au 2026-12-31 (exclu), révisé à 360000 le 2026-07-01 ; B : jamais loué ; C : 200000 depuis le 2026-11-16 sans fin
BA=$(mkbail $A "2026-01-01T00:00:00.000Z" "2026-12-31T00:00:00.000Z" 300000)
BC=$(mkbail $C "2026-11-16T00:00:00.000Z" "" 200000)
echo "  immeuble=$IID (A=$A B=$B C=$C) vide=$IVID  baux A=$BA C=$BC"

echo "── Niveau 3 : baux déjà commencés ──"
req GET /baux/$BA; ok "bail avec date de début passée accepté" "d['data'][0]['enCours'] == True"
req POST /baux/$BA/revision '{"nouveauMontant":360000,"dateEffet":"2026-07-01"}'
ok "révision déjà effective -> loyer courant mis à jour" "float(d['data'][0]['montantLoyer']) == 360000.0"
PAST=$(mkapt P 25.0 1 $IID)
BP=$(mkbail $PAST "2024-01-01T00:00:00.000Z" "2024-03-01T00:00:00.000Z" 100000)
req POST /baux/cloture; ok "F16 sans SQL : la clôture traite le bail expiré" "d['bauxClotures'] >= 1"
req GET /baux/$BP; ok "bail expiré passé en TERMINE" "d['data'][0]['statut'] == 'TERMINE' and d['data'][0]['enCours'] == False"

echo "── F17 - appartements libres ──"
libres() { # date -> liste des noms libres de notre immeuble
  req GET "/appartements/libres${1:+?date=$1}"
  LIB=$(echo "$BODY_OUT" | jq_ "sorted(a['nom'] for g in d['data'] if g['immeubleId']==$IID for a in g['appartements'])")
}
libres 2026-06-01; [ "$LIB" = "['B$S', 'C$S', 'P$S']" ] && { PASS=$((PASS+1)); echo "  ✅ 2026-06-01 : libres = B, C, P (A occupé)"; } || { FAIL=$((FAIL+1)); echo "  ❌ 2026-06-01 : $LIB"; }
libres 2026-12-01; [ "$LIB" = "['B$S', 'P$S']" ] && { PASS=$((PASS+1)); echo "  ✅ 2026-12-01 : libres = B, P (A et C occupés)"; } || { FAIL=$((FAIL+1)); echo "  ❌ 2026-12-01 : $LIB"; }
libres 2026-12-31; [ "$LIB" = "['A$S', 'B$S', 'P$S']" ] && { PASS=$((PASS+1)); echo "  ✅ 2026-12-31 : A libre (fin exclue), C occupé"; } || { FAIL=$((FAIL+1)); echo "  ❌ 2026-12-31 : $LIB"; }
req GET "/appartements/libres?date=2026-12-31"
ok "groupés par immeuble, avec surface et dernier loyer (A = 360000 révisé)" \
   "[a for g in d['data'] if g['immeubleId']==$IID for a in g['appartements'] if a['nom']=='A$S'][0]['dernierLoyer']==360000 and [a for g in d['data'] if g['immeubleId']==$IID for a in g['appartements'] if a['nom']=='A$S'][0]['surface']==50"
req GET "/appartements/libres?date=2026-12-31"
ok "appartement jamais loué : dernier loyer null" "[a for g in d['data'] if g['immeubleId']==$IID for a in g['appartements'] if a['nom']=='B$S'][0].get('dernierLoyer') is None"
req GET "/appartements/libres"; code "sans paramètre (date du jour)" 200
req GET "/appartements/libres?date=pas-une-date"; code "date invalide" 400

echo "── F18 - occupation ──"
req GET /appartements/$A/occupation
ok "A : 2 périodes occupées (300000 puis 360000) puis vacance ouverte" \
   "[(p['type'],p['dateDebut'],p.get('dateFin'),p.get('loyer')) for p in d['data']] == [('OCCUPE','2026-01-01','2026-07-01',300000),('OCCUPE','2026-07-01','2026-12-31',360000),('VACANT','2026-12-31',None,None)]"
req GET /appartements/$B/occupation
ok "B jamais loué : une seule période de vacance depuis sa création" "len(d['data'])==1 and d['data'][0]['type']=='VACANT' and d['data'][0].get('dateFin') is None"
req GET /appartements/$C/occupation
ok "C : vacance avant le bail, puis occupé sans fin" "[p['type'] for p in d['data']]==['VACANT','OCCUPE'] and d['data'][0]['dateFin']=='2026-11-16' and d['data'][1].get('dateFin') is None and d['data'][1]['locataire']['id']==$LID"
req GET /appartements/999999/occupation; code "appartement inconnu" 404

echo "── F19 - revenu d'un immeuble ──"
# nov. 2026 (30 j) : A = 360000 ; C = 200000 x 15/30 = 100000 ; B et P vacants -> 460000 ; occupation = 45 / (4x30) = 37.50 %
req GET "/immeubles/$IID/revenu?mois=2026-11"
ok "revenu total, prorata des jours, taux d'occupation" "float(d['data'][0]['revenuTotal'])==460000.0 and float(d['data'][0]['tauxOccupation'])==37.5 and d['data'][0]['nbAppartements']==4"
req GET "/immeubles/$IID/revenu?mois=2026-11"
ok "détail par appartement (A=360000/30j, C=100000/15j)" "{x['nom']:(float(x['revenu']),x['joursOccupes']) for x in d['data'][0]['details']}=={'A$S':(360000.0,30),'B$S':(0.0,0),'C$S':(100000.0,15),'P$S':(0.0,0)}"
req GET "/immeubles/$IID/revenu?mois=2026-03"
ok "mois avant la révision : loyer de l'époque (300000)" "float(d['data'][0]['revenuTotal'])==300000.0"
req GET "/immeubles/$IID/revenu?mois=2026-07"
ok "mois de la révision : 300000 x 0 + 360000 (révision au 1er)" "float(d['data'][0]['revenuTotal'])==360000.0"
req GET "/immeubles/$IVID/revenu?mois=2026-11"
ok "immeuble entièrement vacant : 0 et 0 %" "float(d['data'][0]['revenuTotal'])==0.0 and float(d['data'][0]['tauxOccupation'])==0.0"
req GET "/immeubles/$IID/revenu?mois=novembre"; code "mois invalide" 400
req GET "/immeubles/999999/revenu?mois=2026-11"; code "immeuble inconnu" 404

echo; echo "Résultat : $PASS réussis, $FAIL échecs"
[ $FAIL -eq 0 ]
