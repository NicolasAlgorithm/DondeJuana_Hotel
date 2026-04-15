# Mini reporte de carga - 100 reservas concurrentes

## 1. Objetivo
Validar el comportamiento de la API de reservas bajo 100 solicitudes concurrentes simuladas y documentar latencia, errores y estabilidad.

## 2. Entorno de prueba
- Proyecto: DondeJuana_Hotel
- Fecha: 15/04/2026
- Rama/commit: load_test
- Equipo: ELPRODIGIOPC (local)
- Base URL: http://localhost:8080
- Motor BD: Oracle ADB
- Usuario de prueba: admin

## 3. Escenario ejecutado
- Herramienta: k6
- Script: k6/reservas_100_concurrentes.js
- Executor: shared-iterations
- VUs: 100
- Iteraciones: 100 (1 reserva por VU)
- Thresholds:
  - http_req_failed < 5%
  - p95 < 1500 ms
  - p99 < 3000 ms

## 4. Comando ejecutado
```bash
K6_USER=admin K6_PASS=admin123 BASE_URL=http://localhost:8080 /c/tools/k6.exe run k6/reservas_100_concurrentes.js --summary-export k6/resultado_100.json
```

## 5. Resultados clave
- Total requests: 102
- Success (201): 100/100 checks de creacion
- Error rate (http_req_failed): 0.00%
- Latencia promedio (http_req_duration avg): 3830 ms
- Latencia p95: 4740 ms
- Latencia p99: 4790 ms
- Throughput (http_reqs): 18.57 req/s

## 6. Evaluacion del objetivo
- Objetivo principal (100 concurrentes): [x] Cumplido  [ ] Parcial  [ ] No cumplido
- Estabilidad general: [x] Estable  [ ] Inestable
- Comentario corto:
Se logro ejecutar 100 VUs concurrentes con 0% de errores HTTP. Sin embargo, el desempeno no cumplio los thresholds de latencia definidos (p95 y p99).

## 7. Hallazgos tecnicos
- Cuello de botella principal detectado:
Latencia elevada en operaciones de creacion de reservas bajo concurrencia (mediana ~3.97s y p95 ~4.74s), posiblemente asociada a contencion en base de datos/transacciones.

- Errores observados (si aplica):
No hubo errores HTTP en la corrida final con usuario admin. Se observo 401 Unauthorized al intentar autenticar con usuario recepcion.

## 8. Recomendaciones
1. Revisar credenciales/permisos del usuario recepcion para habilitar pruebas por rol de recepcionista.
2. Medir tiempos de consultas SQL y bloqueos en Oracle ADB durante la creacion de reservas para ubicar el cuello de botella.
3. Ejecutar una segunda corrida con rampa (20-50-100 VUs) y comparar p95/p99 para validar degradacion progresiva.

## 9. Evidencias
- Archivo JSON de salida: k6/resultado_100.json
- Captura/consola k6: corrida adjunta en terminal Git Bash (threshold de latencia no cumplido)
