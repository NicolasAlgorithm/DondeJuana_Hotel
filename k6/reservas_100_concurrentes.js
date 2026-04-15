import http from 'k6/http';
import { check } from 'k6';
import encoding from 'k6/encoding';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const USERNAME = __ENV.K6_USER || 'recepcion';
const PASSWORD = __ENV.K6_PASS || 'recep123';

export const options = {
  scenarios: {
    reservas_concurrentes_100: {
      executor: 'shared-iterations',
      vus: 100,
      iterations: 100,
      maxDuration: '2m',
    },
  },
  thresholds: {
    http_req_failed: ['rate<0.05'],
    http_req_duration: ['p(95)<1500', 'p(99)<3000'],
  },
};

function authHeaders() {
  const token = encoding.b64encode(`${USERNAME}:${PASSWORD}`);
  return {
    Authorization: `Basic ${token}`,
    'Content-Type': 'application/json',
  };
}

function parseIdsFromListResponse(res, preferredField) {
  if (res.status !== 200) {
    return [];
  }

  const body = res.json();
  if (!Array.isArray(body)) {
    return [];
  }

  return body
    .map((item) => item[preferredField])
    .filter((v) => typeof v === 'number');
}

export function setup() {
  const headers = authHeaders();

  const personasRes = http.get(`${BASE_URL}/api/personas`, { headers });
  const habitacionesRes = http.get(`${BASE_URL}/api/habitaciones`, { headers });

  const personas = parseIdsFromListResponse(personasRes, 'idPersona');
  const habitaciones = parseIdsFromListResponse(habitacionesRes, 'idHabitacion');

  // Fallback seguro para ambientes con seed basico.
  const personaIds = personas.length > 0 ? personas : [1, 2];
  const habitacionIds = habitaciones.length > 0 ? habitaciones : [1, 2, 3];

  return { personaIds, habitacionIds };
}

export default function (data) {
  const headers = authHeaders();

  const personaId = data.personaIds[(__VU - 1) % data.personaIds.length];
  const habitacionId = data.habitacionIds[(__VU - 1) % data.habitacionIds.length];

  // Fechas desplazadas por VU para minimizar choques por solapamiento.
  const entrada = new Date();
  entrada.setDate(entrada.getDate() + __VU + 1);

  const salida = new Date(entrada);
  salida.setDate(salida.getDate() + 1);

  const payload = JSON.stringify({
    idPersona: personaId,
    idHabitacion: habitacionId,
    fechaEntrada: entrada.toISOString().slice(0, 10),
    fechaSalida: salida.toISOString().slice(0, 10),
    estado: 'ACTIVA',
  });

  const res = http.post(`${BASE_URL}/api/reservas`, payload, { headers });

  check(res, {
    'crear reserva: status 201': (r) => r.status === 201,
  });
}
