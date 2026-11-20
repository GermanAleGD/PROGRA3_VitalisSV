#!/usr/bin/env bash
# Prueba manual de la API de citas. Uso: ./scripts/probar-api.sh [AAAA-MM-DD]
# La fecha debe ser un dia futuro entre lunes y viernes (Ana atiende 08:00-12:00).
BASE="http://localhost:8080/api"
FECHA="${1:-2026-11-24}"
RESP="$(mktemp)"

agendar() {  # $1 = JSON
  curl -s -o "$RESP" -w "%{http_code}" -X POST "$BASE/citas" \
    -H "Content-Type: application/json" -d "$1"
}
json() {  # $1=inicio $2=fin
  echo "{\"pacienteId\":1,\"medicoId\":1,\"inicio\":\"${FECHA}T$1\",\"fin\":\"${FECHA}T$2\",\"motivo\":\"Prueba\"}"
}
fila() {  # $1=escenario $2=esperado $3=obtenido
  local res="OK"; [ "$2" != "$3" ] && res="DIFERENTE"
  printf "%-42s esperado %s  obtenido %s  %s\n" "$1" "$2" "$3" "$res"
}

echo "Fecha de prueba: $FECHA"
echo

c=$(agendar "$(json 10:00:00 10:30:00)")
fila "Agendar cita valida (10:00-10:30)" 201 "$c"
ID=$(grep -o '"id":[0-9]*' "$RESP" | head -1 | cut -d: -f2)

c=$(agendar "$(json 10:00:00 10:30:00)")
fila "Repetir la misma cita" 409 "$c"

c=$(agendar "$(json 10:15:00 10:45:00)")
fila "Cita solapada (10:15-10:45)" 409 "$c"

c=$(agendar "$(json 10:30:00 11:00:00)")
fila "Cita contigua (10:30-11:00)" 201 "$c"

c=$(agendar "$(json 13:00:00 13:30:00)")
fila "Fuera de horario (13:00-13:30)" 409 "$c"

c=$(agendar "{\"pacienteId\":1,\"inicio\":\"${FECHA}T09:00:00\",\"fin\":\"${FECHA}T09:30:00\"}")
fila "Datos incompletos (sin medicoId)" 400 "$c"

c=$(curl -s -o /dev/null -w "%{http_code}" -X PATCH "$BASE/citas/$ID/cancelar")
fila "Cancelar la primera cita (id $ID)" 200 "$c"

c=$(agendar "$(json 10:00:00 10:30:00)")
fila "Volver a agendar el horario liberado" 201 "$c"

rm -f "$RESP"
