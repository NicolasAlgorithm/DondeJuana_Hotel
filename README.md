# Hotel Donde Juana (Spring Boot + Oracle ADB)

Sistema de gestión hotelera con Spring Boot 3, Thymeleaf, Spring Security, JWT y Oracle Autonomous Database.

## Requisitos

- Java 21 (LTS)
- Maven (o usar el Maven Wrapper incluido)
- Oracle Autonomous Database con wallet descargado

## Configuración del Wallet y conexión Oracle

### 1. Descargar el wallet

Descarga el wallet desde la consola de OCI (Oracle Cloud Infrastructure):
- Autonomous Database → tu instancia → DB Connection → Download wallet

Descomprime el wallet dentro del proyecto en esta ruta:
- `wallet/Wallet_DondeJuanaDB`

El wallet contiene archivos como: `cwallet.sso`, `ewallet.p12`, `tnsnames.ora`, `sqlnet.ora`, `ojdbc.properties`, etc.
La aplicación detecta esta carpeta automáticamente al iniciar.

### 2. Variables de entorno (opcionales)

La app ya incluye valores por defecto para conexión (usuario y contraseña) y puede iniciar sin exportar variables.
Si quieres sobrescribirlos en otro ambiente, usa estas variables:

| Variable | Descripción | Ejemplo (Windows) |
|---|---|---|
| `DB_USERNAME` | Usuario de conexión en Oracle | `HOTEL` |
| `DB_PASSWORD` | Contraseña del usuario de conexión | `tuPassword` |
| `APP_TEMPLATES_EXTERNAL_PREFIX` | Ruta de plantillas externas | `file:./` o `file:/opt/app/` |
| `APP_TEMPLATES_CACHE` | Cache de plantillas Thymeleaf | `true` |
| `SESSION_COOKIE_SECURE` | Cookie `JSESSIONID` solo HTTPS | `true` |
| `JWT_SECRET` | Secreto para firmar JWT (>= 32 bytes) | `tu_secreto_largo_de_32_caracteres_minimo` |
| `JWT_EXPIRATION_MINUTES` | Expiracion del JWT en minutos | `30` |

`TNS_ADMIN` quedo como opcional: si existe, la app lo usa; si no, usa `wallet/Wallet_DondeJuanaDB`.

La vista principal se carga desde `index.html` en la raiz del proyecto mediante Thymeleaf.

### 3. Grants y sinonimos en Oracle (REQUERIDO)

Las tablas `USUARIOS` y `ROLES` estan en el esquema `ADMIN`.
La aplicacion se conecta como usuario `HOTEL`. Para que `HOTEL` pueda leer esas tablas,
ejecuta esto **una sola vez** conectado como `ADMIN` (o un DBA):

```sql
-- Dar permisos de lectura a HOTEL
GRANT SELECT ON ADMIN.USUARIOS TO HOTEL;
GRANT SELECT ON ADMIN.ROLES    TO HOTEL;

-- Crear sinonimos en HOTEL (opcional, si se quita default_schema de application.properties)
-- CREATE OR REPLACE SYNONYM HOTEL.USUARIOS FOR ADMIN.USUARIOS;
-- CREATE OR REPLACE SYNONYM HOTEL.ROLES    FOR ADMIN.ROLES;
```

> **Nota:** La propiedad `spring.jpa.properties.hibernate.default_schema=ADMIN` en
> `application.properties` hace que Hibernate prefije todas las consultas con `ADMIN.`,
> por lo que los sinonimos son opcionales si los grants ya existen.

## Ejecutar

### Linux / Mac

```bash
./mvnw spring-boot:run
```

### Windows (cmd)

```cmd
.\mvnw.cmd spring-boot:run
```

### Windows (PowerShell)

```powershell
.\mvnw.cmd spring-boot:run
```

Luego abrir en el navegador: `http://localhost:8080/`

## Docker (local y produccion)

Se agrego configuracion lista para contenedores:

- `Dockerfile`
- `.dockerignore`
- `render.yaml` (Blueprint para Render)

### Build y run local con Docker

```bash
docker build -t dondejuana-hotel .
docker run --rm -p 8080:8080 \
  -e DB_USERNAME=HOTEL \
  -e DB_PASSWORD=tuPassword \
  -e JWT_SECRET=tu_secreto_largo_de_32_caracteres_minimo \
  dondejuana-hotel
```

## Deploy en Render (Web Service)

### Opcion A: Usando `render.yaml` (recomendado)

1. En Render: **New +** → **Blueprint**.
2. Conecta tu repo de GitHub.
3. Render detectara `render.yaml` y creara el Web Service Docker.
4. Configura variables secretas en el panel:
   - `DB_USERNAME`
   - `DB_PASSWORD`
   - `JWT_SECRET`
5. Deploy.

### Opcion B: Manual

1. En Render: **New +** → **Web Service**.
2. Selecciona el repo.
3. Environment: **Docker**.
4. Dockerfile path: `./Dockerfile`.
5. Variables de entorno: `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`.
6. Deploy.

> Importante: GitHub Pages y Netlify (modo estatico) no ejecutan Spring Boot, por eso no funcionan login/sesiones/Thymeleaf de este proyecto.

## Autenticacion y seguridad

El sistema usa login con usuarios almacenados en la tabla `ADMIN.USUARIOS` (BCrypt).
- Usuario activo (`ACTIVO = 'S'`) con rol activo (`ROLES.ACTIVO = 'S'`) puede iniciar sesion.
- Usuario o rol inactivo (`ACTIVO = 'N'`) no puede autenticarse.

La pagina principal (`/`) muestra el estado de la conexion a BD:
- ✅ **DB Status: OK** — la app se conecto correctamente y puede leer `ADMIN.USUARIOS`.
- ❌ **DB Status: ERROR** — problema de conexion o permisos; se muestra el mensaje de error.

### API REST

Los endpoints `/api/**` aceptan autenticacion **HTTP Basic** o **JWT**.
CSRF esta deshabilitado para toda la API para permitir clientes REST.

#### Obtener token JWT

```
POST /api/auth/token
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

Respuesta:

```json
{
  "tokenType": "Bearer",
  "accessToken": "<token>",
  "expiresInSeconds": 1800
}
```

Uso:

```
Authorization: Bearer <token>
```

> El secreto `JWT_SECRET` debe tener al menos 32 bytes (256 bits). Si usas Base64, puede ser una cadena Base64 valida.

## Inventario de endpoints REST

### Auth
- `POST /api/auth/token`

### Tipos de habitacion — `/api/tipos-habitacion`
- `GET /api/tipos-habitacion`
- `GET /api/tipos-habitacion/{id}`
- `POST /api/tipos-habitacion`
- `PUT /api/tipos-habitacion/{id}`
- `DELETE /api/tipos-habitacion/{id}`

### Habitaciones — `/api/habitaciones`
- `GET /api/habitaciones`
- `GET /api/habitaciones/{id}`
- `POST /api/habitaciones`
- `PUT /api/habitaciones/{id}`
- `PATCH /api/habitaciones/{id}/estado`
- `DELETE /api/habitaciones/{id}`

### Personas — `/api/personas`
- `GET /api/personas`
- `GET /api/personas/{id}`
- `POST /api/personas`
- `PUT /api/personas/{id}`
- `DELETE /api/personas/{id}`

### Reservas — `/api/reservas`
- `GET /api/reservas`
- `GET /api/reservas/{id}`
- `POST /api/reservas`
- `PUT /api/reservas/{id}`
- `PATCH /api/reservas/{id}/cancelar`
- `PATCH /api/reservas/{id}/checkin`
- `PATCH /api/reservas/{id}/checkout` (param opcional `fechaSalidaReal=yyyy-MM-dd`)
- `GET /api/reservas/disponibilidad`
- `DELETE /api/reservas/{id}`

### Calendario — `/api/calendario`
- `GET /api/calendario?fechaInicio=yyyy-MM-dd&fechaFin=yyyy-MM-dd`

## API Calendario de Reservas

El endpoint `/api/calendario` devuelve, para un rango de fechas dado, el estado
de **cada habitacion activa** en **cada dia** del rango. La informacion incluye
un codigo de color hexadecimal CSS listo para mapear a un calendario.

### Parametros de consulta

| Parametro | Tipo | Obligatorio | Descripcion |
|---|---|---|---|
| `fechaInicio` | `yyyy-MM-dd` | ✅ | Primer dia del rango (incluido). |
| `fechaFin` | `yyyy-MM-dd` | ✅ | Ultimo dia del rango (excluido). |
| `idTipoHabitacion` | `Long` | ❌ | Filtra solo habitaciones del tipo indicado. |
| `piso` | `Integer` | ❌ | Filtra solo habitaciones del piso indicado. |

**Restricciones:**
- `fechaInicio` debe ser estrictamente anterior a `fechaFin`.
- El rango maximo es de **90 dias**.

### Estados de habitacion y colores

| Codigo de estado | Etiqueta | Color sugerido | Hex |
|---|---|---|---|
| `DISPONIBLE` | Disponible | verde | `#28a745` |
| `OCUPADA` | Ocupada | rojo | `#dc3545` |
| `MANTENIMIENTO` | En mantenimiento | amarillo | `#ffc107` |
| `FUERA_DE_SERVICIO` | Fuera de servicio | gris | `#6c757d` |

### Ejemplos de uso

```
GET /api/calendario?fechaInicio=2026-04-01&fechaFin=2026-05-01
GET /api/calendario?fechaInicio=2026-04-07&fechaFin=2026-04-14&idTipoHabitacion=2
GET /api/calendario?fechaInicio=2026-04-07&fechaFin=2026-04-14&piso=3
```

### Seguridad

Requiere sesion autenticada (Basic o JWT) y rol **`ROLE_ADMINISTRADOR`** o **`ROLE_RECEPCIONISTA`**
(o permiso `calendario.ver`).

## Postman y pruebas API

### Prerrequisitos

1. **JDK 21** instalado y en el PATH.
2. **Oracle ADB** con wallet configurado (`TNS_ADMIN` + `tnsnames.ora`).
3. **Postman** (version 10 o superior) instalado.

### Paso 1: Ejecutar datos semilla en Oracle

```sql
@sql/postman_seed.sql
```

Esto crea:
- Roles: `ADMINISTRADOR` (id=1), `RECEPCIONISTA` (id=2)
- Usuario: `admin` / `admin123` (ADMINISTRADOR)
- Usuario: `recepcion` / `recep123` (RECEPCIONISTA)
- Tipos de habitacion (IDs 1, 2, 3)
- Habitaciones (IDs 1, 2, 3)
- Huespedes (IDs 1, 2)

### Paso 2: Arrancar la aplicacion

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

### Paso 3: Importar en Postman

1. Abre Postman → **Import**.
2. Selecciona `postman/DondeJuana_Hotel.postman_collection.json`.
3. Importa `postman/local.postman_environment.json`.
4. Selecciona el entorno **DondeJuana Hotel - Local**.

### Paso 4: Configurar variables de entorno

| Variable | Valor por defecto | Descripcion |
|---|---|---|
| `baseUrl` | `http://localhost:8080` | URL del backend local |
| `username` | `admin` | Usuario para HTTP Basic Auth |
| `password` | `admin123` | Contraseña |
| `personaSeedId` | `1` | ID del huesped seed |
| `habitacionSeedId` | `1` | ID de la habitacion seed |
| `tipoHabitacionSeedId` | `1` | ID del tipo de habitacion seed |

### Paso 5: Ejecutar las pruebas

Opcion recomendada: **Run collection** en el Collection Runner. Los modulos estan numerados (00 → 04).

### Autenticacion en Postman

La coleccion usa **HTTP Basic Auth** configurado a nivel de coleccion con las variables
`{{username}}` y `{{password}}`. Tambien puedes usar JWT:

1. Ejecuta `POST /api/auth/token`.
2. Copia `accessToken`.
3. Agrega el header `Authorization: Bearer <token>`.

## Errores comunes

| Error | Causa | Solucion |
|---|---|---|
| `401 Unauthorized` | Credenciales incorrectas o sin autenticacion | Verifica usuario y contraseña, o JWT | 
| `400 Bad Request: "Estado invalido"` | Estado de habitacion no permitido | Usa: `DISPONIBLE`, `OCUPADA`, o `MANTENIMIENTO` |
| `400 Bad Request: "Estado invalido" en reservas` | Estado de reserva no permitido | Usa: `ACTIVA`, `EN_ESTADIA`, `CANCELADA`, o `CUMPLIDA` |
| `400 Bad Request: "La habitacion no esta disponible"` | Traslape de fechas | Cambia el rango de fechas o usa otra habitacion |
| `404 Not Found` | ID no existe en BD | Ejecuta el seed SQL o usa un ID existente |
| `Connection refused` | App no esta corriendo | Ejecuta `./mvnw spring-boot:run` |

## Pruebas unitarias

El repositorio usa **JUnit 5 + Mockito** con `spring-boot-starter-test`.

### Ejecutar pruebas

```bash
./mvnw test
```

### Alcance cubierto (suite actual)

- **Backend (servicios criticos):**
  - `ReservaService`
  - `CalendarioService`
  - `HabitacionServiceImpl`
  - `DbUserDetailsService`
- **Frontend MVC (flujo UI en controladores):**
  - `ReservaController`
  - `CalendarioController`
  - `HabitacionMvcController`
  - `IndexController`

## Pruebas E2E (integracion)

Se agrego la suite `E2EFlujosIntegracionTest` para validar de extremo a extremo:

- Login (exito, error y sesion)
- Reserva (creacion y consulta)
- Check-in / Check-out (transicion completa de estado)
- Pago (validacion de monto total al completar el flujo)
- Reportes (ocupacion en calendario)

### Script para ejecutar solo E2E

```bash
./mvnw test -Pe2e
```

### Script alternativo (clase puntual)

```bash
./mvnw test -Dtest=E2EFlujosIntegracionTest
```

## Estructura del proyecto

```
src/
└── main/
    ├── java/com/project/hotel/
    │   ├── MvcpersonasApplication.java
    │   ├── config/
    │   │   ├── JwtAuthenticationFilter.java
    │   │   └── SecurityConfig.java
    │   ├── controller/
    │   │   ├── ApiAuthController.java        ← POST /api/auth/token
    │   │   ├── AuthController.java           ← GET /login
    │   │   ├── CalendarioController.java     ← GET /api/calendario
    │   │   ├── IndexController.java          ← GET / (con DB health check)
    │   │   ├── HabitacionController.java     ← /api/habitaciones
    │   │   ├── PersonaApiController.java     ← /api/personas
    │   │   ├── ReservaApiController.java     ← /api/reservas
    │   │   └── TipoHabitacionApiController.java ← /api/tipos-habitacion
    │   ├── entities/
    │   │   ├── Usuario.java                  ← Mapea ADMIN.USUARIOS
    │   │   ├── Rol.java                      ← Mapea ADMIN.ROLES
    │   │   ├── Habitacion.java
    │   │   ├── Persona.java
    │   │   ├── Reserva.java
    │   │   └── TipoHabitacion.java
    │   ├── repository/
    │   └── service/
    │       ├── DbUserDetailsService.java
    │       └── JwtService.java
    └── resources/
      ├── application.properties
      └── templates/
        └── auth/login.html

  index.html                                  ← Vista principal en la raiz del proyecto
```
