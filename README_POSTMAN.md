# Pruebas de Endpoints en Postman — Hotel Donde Juana

Guía completa para importar la colección Postman, ejecutar los datos semilla y validar
todos los endpoints REST del backend.

---

## Clases tocadas / creadas

| Archivo | Ruta | Cambio |
|---|---|---|
| `SecurityConfig.java` | `src/main/java/com/project/hotel/config/SecurityConfig.java` | Habilitado HTTP Basic Auth para `/api/**`; CSRF ignorado para toda la API |
| `ReservaApiController.java` | `src/main/java/com/project/hotel/controller/ReservaApiController.java` | Añadidos `GET /api/reservas` y `GET /api/reservas/{id}` |
| `HabitacionController.java` | `src/main/java/com/project/hotel/controller/HabitacionController.java` | Añadido `DELETE /api/habitaciones/{id}` |
| `HabitacionService.java` | `src/main/java/com/project/hotel/service/HabitacionService.java` | Añadido método `eliminar(Long id)` a la interfaz |
| `HabitacionServiceImpl.java` | `src/main/java/com/project/hotel/service/impl/HabitacionServiceImpl.java` | Implementación de `eliminar(Long id)` |
| **`PersonaApiController.java`** *(nuevo)* | `src/main/java/com/project/hotel/controller/PersonaApiController.java` | Controller REST para `/api/personas/**` (CRUD completo) |
| **`TipoHabitacionApiController.java`** *(nuevo)* | `src/main/java/com/project/hotel/controller/TipoHabitacionApiController.java` | Controller REST para `/api/tipos-habitacion/**` (CRUD completo) |
| **`PersonaRequest.java`** *(nuevo)* | `src/main/java/com/project/hotel/dto/PersonaRequest.java` | DTO para crear/actualizar huéspedes vía API |
| **`TipoHabitacionRequest.java`** *(nuevo)* | `src/main/java/com/project/hotel/dto/TipoHabitacionRequest.java` | DTO para crear/actualizar tipos de habitación vía API |

---

## Inventario completo de endpoints REST

### AuthController — `/login`
| Método | Ruta | Descripción | HTTP esperado |
|--------|------|-------------|---------------|
| GET | `/login` | Muestra formulario de login (MVC) | 200 |
| POST | `/login` | Autentica con form-urlencoded | 302 (redirect a /) |

### TipoHabitacionApiController — `/api/tipos-habitacion`
| Método | Ruta | Descripción | HTTP esperado |
|--------|------|-------------|---------------|
| GET | `/api/tipos-habitacion` | Lista todos los tipos | 200 |
| GET | `/api/tipos-habitacion/{id}` | Obtiene un tipo por ID | 200 / 404 |
| POST | `/api/tipos-habitacion` | Crea un tipo nuevo | 201 |
| PUT | `/api/tipos-habitacion/{id}` | Actualiza un tipo | 200 / 404 |
| DELETE | `/api/tipos-habitacion/{id}` | Elimina un tipo | 204 / 404 |

### HabitacionController — `/api/habitaciones`
| Método | Ruta | Descripción | HTTP esperado |
|--------|------|-------------|---------------|
| GET | `/api/habitaciones` | Lista todas las habitaciones | 200 |
| GET | `/api/habitaciones/{id}` | Obtiene una habitación por ID | 200 / 404 |
| POST | `/api/habitaciones` | Crea una habitación | 201 |
| PUT | `/api/habitaciones/{id}` | Actualiza una habitación | 200 / 400 |
| PATCH | `/api/habitaciones/{id}/estado` | Cambia estado (DISPONIBLE/OCUPADA/MANTENIMIENTO) | 200 / 400 |
| DELETE | `/api/habitaciones/{id}` | Elimina una habitación | 204 / 404 |

### PersonaApiController — `/api/personas`
| Método | Ruta | Descripción | HTTP esperado |
|--------|------|-------------|---------------|
| GET | `/api/personas` | Lista todos los huéspedes | 200 |
| GET | `/api/personas/{id}` | Obtiene un huésped por ID | 200 / 404 |
| POST | `/api/personas` | Crea un huésped | 201 |
| PUT | `/api/personas/{id}` | Actualiza un huésped | 200 / 404 |
| DELETE | `/api/personas/{id}` | Elimina un huésped | 204 / 404 |

### ReservaApiController — `/api/reservas`
| Método | Ruta | Descripción | HTTP esperado |
|--------|------|-------------|---------------|
| GET | `/api/reservas` | Lista todas las reservas | 200 |
| GET | `/api/reservas/{id}` | Obtiene una reserva por ID | 200 / 404 |
| POST | `/api/reservas` | Crea una reserva | 201 |
| PUT | `/api/reservas/{id}` | Modifica una reserva | 200 / 400 |
| PATCH | `/api/reservas/{id}/cancelar` | Cancela una reserva activa | 200 / 400 |
| GET | `/api/reservas/disponibilidad` | Verifica disponibilidad de habitación | 200 / 400 |
| DELETE | `/api/reservas/{id}` | Elimina una reserva | 204 / 400 |

---

## Prerrequisitos

1. **JDK 21** instalado y en el PATH.
2. **Oracle ADB** con wallet configurado (`TNS_ADMIN` + `tnsnames.ora`).
3. **Postman** (versión 10 o superior) instalado.

---

## Paso 1: Ejecutar datos semilla en Oracle

Abre SQL*Plus, SQL Developer, o tu cliente Oracle preferido y ejecuta:

```sql
@sql/postman_seed.sql
```

Esto crea:
- Roles: `ADMINISTRADOR` (id=1), `RECEPCIONISTA` (id=2)
- Usuario: `admin` / `admin123` (ADMINISTRADOR)
- Usuario: `recepcion` / `recep123` (RECEPCIONISTA)
- 3 tipos de habitación (IDs 1, 2, 3)
- 3 habitaciones (IDs 1, 2, 3)
- 2 huéspedes (IDs 1, 2)

> **Nota:** Si la BD ya tiene datos, el script usa `MERGE INTO` para no duplicar.

---

## Paso 2: Arrancar la aplicación

```powershell
# Windows PowerShell
$env:TNS_ADMIN = "C:\oracle\oracle\wallet\Wallet_DondeJuanaDB"
.\mvnw spring-boot:run
```

```bash
# Linux / macOS / Git Bash
export TNS_ADMIN="/ruta/al/Wallet_DondeJuanaDB"
./mvnw spring-boot:run
```

Espera a ver: `Started MvcpersonasApplication in X.XXX seconds`.

---

## Paso 3: Importar en Postman

1. Abre Postman → **Import** (botón arriba a la izquierda).
2. Arrastra o selecciona `postman/DondeJuana_Hotel.postman_collection.json`.
3. Vuelve a **Import** y selecciona `postman/local.postman_environment.json`.
4. En la esquina superior derecha, selecciona el entorno **"DondeJuana Hotel - Local"**.

---

## Paso 4: Configurar variables de entorno en Postman

Verifica o ajusta las siguientes variables en el entorno importado:

| Variable | Valor por defecto | Descripción |
|---|---|---|
| `baseUrl` | `http://localhost:8080` | URL del backend local |
| `username` | `admin` | Usuario para HTTP Basic Auth |
| `password` | `admin123` | Contraseña |
| `personaSeedId` | `1` | ID del huésped seed |
| `habitacionSeedId` | `1` | ID de la habitación seed |
| `tipoHabitacionSeedId` | `1` | ID del tipo de habitación seed |

---

## Paso 5: Ejecutar las pruebas

### Opción A — Ejecutar toda la colección (Postman Collection Runner)

1. Click derecho en la colección → **Run collection**.
2. Orden recomendado: los módulos ya están numerados (00 → 04).
3. Haz clic en **Run DondeJuana Hotel API**.
4. Revisa los resultados en verde (pass) / rojo (fail).

### Opción B — Ejecutar módulo por módulo

Ejecuta en orden para que las variables de colección se encadenen:

```
00. Auth
  → Login exitoso (form)
  → Login fallido
  → Basic Auth sin credenciales (401)

01. Tipos de Habitación
  → Listar → Crear (guarda tipoId) → Obtener → Actualizar → 404 → Eliminar

02. Habitaciones
  → Listar → Crear (guarda habitacionId) → Obtener → Actualizar
  → Actualizar estado → Estado inválido (400) → 404 → Eliminar

03. Huéspedes (Personas)
  → Listar → Crear (guarda personaId) → Obtener → Actualizar
  → 404 → Campos vacíos (400) → Eliminar

04. Reservas
  → Listar → Disponibilidad → Crear (guarda reservaId) → Obtener
  → Modificar → Cancelar → 404 → Fechas inválidas (400) → Eliminar
```

---

## Autenticación

La colección usa **HTTP Basic Auth** configurado a nivel de colección con las variables
`{{username}}` y `{{password}}`. Todos los endpoints `/api/**` requieren autenticación
(rol `ADMINISTRADOR` o `RECEPCIONISTA`).

Los endpoints de autenticación de formulario (`/login`) tienen `auth: noauth` para
no enviar el header Basic.

---

## Errores comunes

| Error | Causa | Solución |
|---|---|---|
| `401 Unauthorized` | Credenciales incorrectas o sin autenticación | Verifica `username` y `password` en el entorno |
| `400 Bad Request: "Estado inválido"` | Estado de habitación no permitido | Usa: `DISPONIBLE`, `OCUPADA`, o `MANTENIMIENTO` |
| `400 Bad Request: "Estado inválido" en reservas` | Estado de reserva no permitido | Usa: `ACTIVA`, `CANCELADA`, o `CUMPLIDA` |
| `400 Bad Request: "La habitación no está disponible"` | Traslape de fechas | Cambia el rango de fechas o usa otra habitación |
| `404 Not Found` | ID no existe en BD | Ejecuta el seed SQL o usa un ID existente |
| `Connection refused` | App no está corriendo | Ejecuta `./mvnw spring-boot:run` |

---

## Estructura de archivos Postman

```
postman/
├── DondeJuana_Hotel.postman_collection.json   ← Colección completa (importar)
└── local.postman_environment.json             ← Entorno local (importar)

sql/
├── grants_hotel.sql                           ← Grants y sinónimos Oracle
└── postman_seed.sql                           ← Datos semilla para pruebas
```

---

## Notas técnicas

- Los controladores MVC (`/personas/**`, `/tipos-habitacion/**`) siguen funcionando
  con vistas Thymeleaf para la UI web.
- Los nuevos controladores REST (`/api/personas/**`, `/api/tipos-habitacion/**`)
  coexisten sin conflicto con los MVC.
- El header `WWW-Authenticate: Basic realm="DondeJuana Hotel API"` se envía en
  respuestas 401 para que clientes REST identifiquen el mecanismo de auth.
