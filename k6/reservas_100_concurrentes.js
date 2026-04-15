import http from 'k6/http';
import { check } from 'k6';
import encoding from 'k6/encoding';
import exec from 'k6/execution';
import { Counter } from 'k6/metrics';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const USERNAME = __ENV.K6_USER || 'recepcion';
const PASSWORD = __ENV.K6_PASS || 'recep123';
const RUN_OFFSET_FROM_ENV = Number(__ENV.K6_RUN_OFFSET_DAYS);

const status201 = new Counter('reservas_status_201');
const status400 = new Counter('reservas_status_400');
const status401 = new Counter('reservas_status_401');
const status403 = new Counter('reservas_status_403');
const status409 = new Counter('reservas_status_409');
const status500 = new Counter('reservas_status_500');
const statusOther = new Counter('reservas_status_other');

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
    'http_req_failed{endpoint:crear_reserva}': ['rate<0.05'],
    'http_req_duration{endpoint:crear_reserva}': ['p(95)<1500', 'p(99)<3000'],
  },
};

function resolveRunOffsetDays() {
  if (Number.isFinite(RUN_OFFSET_FROM_ENV) && RUN_OFFSET_FROM_ENV >= 1) {
    return Math.trunc(RUN_OFFSET_FROM_ENV);
  }

  // Usa una ventana futura distinta por corrida para evitar choques con datos de ejecuciones anteriores.
  return 365 + (Math.floor(Date.now() / 1000) % 1200);
}

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

  return {
    personaIds,
    habitacionIds,
    runOffsetDays: resolveRunOffsetDays(),
  };
}

export default function (data) {
  const headers = authHeaders();

  const iterationInTest = exec.scenario.iterationInTest;

  const personaId = data.personaIds[iterationInTest % data.personaIds.length];
  const habitacionId = data.habitacionIds[iterationInTest % data.habitacionIds.length];

  // Fechas desplazadas por corrida e iteracion para evitar solapamiento entre corridas consecutivas.
  const entrada = new Date();
  entrada.setDate(entrada.getDate() + data.runOffsetDays + iterationInTest);

  const salida = new Date(entrada);
  salida.setDate(salida.getDate() + 1);

  const payload = JSON.stringify({
    idPersona: personaId,
    idHabitacion: habitacionId,
    fechaEntrada: entrada.toISOString().slice(0, 10),
    fechaSalida: salida.toISOString().slice(0, 10),
    estado: 'ACTIVA',
  });

  const res = http.post(`${BASE_URL}/api/reservas`, payload, {
    headers,
    tags: { endpoint: 'crear_reserva' },
  });

  if (res.status === 201) {
    status201.add(1);
  } else if (res.status === 400) {
    status400.add(1);
  } else if (res.status === 401) {
    status401.add(1);
  } else if (res.status === 403) {
    status403.add(1);
  } else if (res.status === 409) {
    status409.add(1);
  } else if (res.status >= 500) {
    status500.add(1);
  } else {
    statusOther.add(1);
  }

  if (res.status !== 201 && iterationInTest < 5) {
    console.error(`Reserva fallo: status=${res.status} body=${res.body}`);
  }

  check(res, {
    'crear reserva: status 201': (r) => r.status === 201,
  });
}
