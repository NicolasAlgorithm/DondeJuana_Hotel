# README – API Calendario de Reservas de Habitaciones

Este documento describe el endpoint REST que soporta la vista tipo calendario
de ocupación de habitaciones del hotel **Donde Juana**.

---

## Índice

1. [Descripción general](#descripción-general)
2. [Endpoint GET /api/calendario](#endpoint-get-apicalendario)
3. [Parámetros de consulta](#parámetros-de-consulta)
4. [Estados de habitación y colores](#estados-de-habitación-y-colores)
5. [Ejemplos de uso](#ejemplos-de-uso)
6. [Estructura de la respuesta](#estructura-de-la-respuesta)
7. [Códigos de respuesta HTTP](#códigos-de-respuesta-http)
8. [Seguridad](#seguridad)
9. [Clases nuevas involucradas](#clases-nuevas-involucradas)

---

## Descripción general

El endpoint `/api/calendario` devuelve, para un rango de fechas dado, el estado
de **cada habitación activa** en **cada día** del rango. La información incluye
un código de color hexadecimal CSS listo para ser mapeado a un componente de
frontend tipo agenda/calendario.

La lógica de estado por día sigue estas prioridades:

| Prioridad | Condición | Estado devuelto |
|-----------|-----------|----------------|
| 1 | `activo = 'N'` | `FUERA_DE_SERVICIO` |
| 2 | `estado = 'MANTENIMIENTO'` | `MANTENIMIENTO` |
| 3 | Existe reserva no cancelada que cubre el día | `OCUPADA` |
| 4 | Ninguna de las anteriores | `DISPONIBLE` |

---

## Endpoint GET /api/calendario

```
GET /api/calendario
```

---

## Parámetros de consulta

| Parámetro | Tipo | Obligatorio | Descripción |
|-----------|------|-------------|-------------|
| `fechaInicio` | `yyyy-MM-dd` | ✅ | Primer día del rango (incluido). |
| `fechaFin` | `yyyy-MM-dd` | ✅ | Último día del rango (excluido). |
| `idTipoHabitacion` | `Long` | ❌ | Filtra solo habitaciones del tipo indicado. |
| `piso` | `Integer` | ❌ | Filtra solo habitaciones del piso indicado. |

**Restricciones:**
- `fechaInicio` debe ser estrictamente anterior a `fechaFin`.
- El rango máximo es de **90 días**.

---

## Estados de habitación y colores

| Código de estado | Etiqueta | Color sugerido | Hex |
|------------------|----------|----------------|-----|
| `DISPONIBLE` | Disponible | 🟢 verde | `#28a745` |
| `OCUPADA` | Ocupada | 🔴 rojo | `#dc3545` |
| `MANTENIMIENTO` | En mantenimiento | 🟡 amarillo | `#ffc107` |
| `FUERA_DE_SERVICIO` | Fuera de servicio | ⚫ gris | `#6c757d` |

---

## Ejemplos de uso

### 1. Calendario del mes de abril para todas las habitaciones activas

```
GET /api/calendario?fechaInicio=2026-04-01&fechaFin=2026-05-01
```

### 2. Semana filtrada por tipo de habitación

```
GET /api/calendario?fechaInicio=2026-04-07&fechaFin=2026-04-14&idTipoHabitacion=2
```

### 3. Semana filtrada por piso

```
GET /api/calendario?fechaInicio=2026-04-07&fechaFin=2026-04-14&piso=3
```

### 4. Semana filtrada por tipo y piso a la vez

```
GET /api/calendario?fechaInicio=2026-04-07&fechaFin=2026-04-14&idTipoHabitacion=2&piso=1
```

---

## Estructura de la respuesta

### Respuesta exitosa (`200 OK`)

```json
{
  "fechaInicio": "2026-04-01",
  "fechaFin": "2026-04-08",
  "totalDias": 7,
  "totalHabitaciones": 2,
  "habitaciones": [
    {
      "idHabitacion": 5,
      "codigo": "HAB-105",
      "numero": "105",
      "piso": 1,
      "idTipoHabitacion": 2,
      "nombreTipoHabitacion": "Suite",
      "tarifaNoche": 150.00,
      "dias": [
        {
          "fecha": "2026-04-01",
          "estado": "DISPONIBLE",
          "etiquetaEstado": "Disponible",
          "codigoColor": "#28a745",
          "idReserva": null
        },
        {
          "fecha": "2026-04-02",
          "estado": "OCUPADA",
          "etiquetaEstado": "Ocupada",
          "codigoColor": "#dc3545",
          "idReserva": 42
        },
        {
          "fecha": "2026-04-03",
          "estado": "OCUPADA",
          "etiquetaEstado": "Ocupada",
          "codigoColor": "#dc3545",
          "idReserva": 42
        },
        {
          "fecha": "2026-04-04",
          "estado": "MANTENIMIENTO",
          "etiquetaEstado": "En mantenimiento",
          "codigoColor": "#ffc107",
          "idReserva": null
        }
      ]
    }
  ]
}
```

### Respuesta de error de validación (`400 Bad Request`)

```json
{ "error": "fechaInicio debe ser anterior a fechaFin" }
```

```json
{ "error": "El rango máximo permitido es 90 días (solicitado: 120)" }
```

### Respuesta de error interno (`500 Internal Server Error`)

```json
{ "error": "Error interno al obtener el calendario: <mensaje>" }
```

---

## Códigos de respuesta HTTP

| Código | Descripción |
|--------|-------------|
| `200 OK` | Consulta exitosa; cuerpo con `CalendarioResponse`. |
| `400 Bad Request` | Parámetros inválidos (fechas invertidas, rango excedido). |
| `401 Unauthorized` | Sin autenticación. |
| `403 Forbidden` | Usuario autenticado sin rol `ADMINISTRADOR` o `RECEPCIONISTA`. |
| `500 Internal Server Error` | Error inesperado del servidor. |

---

## Seguridad

El endpoint requiere:
- Sesión autenticada (cookie `JSESSIONID`).
- Rol **`ADMINISTRADOR`** o **`RECEPCIONISTA`**.

CSRF está deshabilitado para `/api/calendario/**`, igual que para
`/api/reservas/**`, de modo que clientes REST puedan consumirlo sin token CSRF.

---

## Clases nuevas involucradas

| Tipo | Clase | Descripción |
|------|-------|-------------|
| Enum (DTO) | `com.project.hotel.dto.EstadoCalendario` | Estados posibles con etiqueta y código de color. |
| DTO | `com.project.hotel.dto.DiaEstadoDTO` | Estado de una habitación en un día concreto. |
| DTO | `com.project.hotel.dto.HabitacionCalendarioDTO` | Habitación con su lista de estados por día. |
| DTO | `com.project.hotel.dto.CalendarioResponse` | Respuesta completa del endpoint (rango + habitaciones). |
| Service | `com.project.hotel.service.CalendarioService` | Lógica de negocio que construye el calendario. |
| Controller | `com.project.hotel.controller.CalendarioController` | Controlador REST (`GET /api/calendario`). |
| Test | `com.project.hotel.CalendarioServiceTest` | Pruebas unitarias del servicio. |
| Test | `com.project.hotel.CalendarioControllerTest` | Pruebas unitarias del controlador. |

### Métodos añadidos a repositorios existentes

**`HabitacionRepository`**

| Método | Descripción |
|--------|-------------|
| `findByActivo(String activo)` | Todas las habitaciones activas. |
| `findByTipoHabitacion_IdTipoAndActivo(Long, String)` | Activas de un tipo concreto. |
| `findByPisoAndActivo(Integer, String)` | Activas de un piso concreto. |
| `findByTipoHabitacion_IdTipoAndPisoAndActivo(Long, Integer, String)` | Activas de un tipo y piso concretos. |

**`ReservaRepository`**

| Método | Descripción |
|--------|-------------|
| `findActivasEnRangoParaHabitaciones(List<Long>, LocalDate, LocalDate)` | Reservas no canceladas que se solapan con el rango dado para un conjunto de habitaciones. |
