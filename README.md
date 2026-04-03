# Hotel Donde Juana (Spring Boot + Oracle ADB)

Sistema de gestión hotelera con Spring Boot 3, Thymeleaf, Spring Security y Oracle Autonomous Database.

## Requisitos

- Java 21+
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

### 2. Variables de entorno (recomendado)

Define las siguientes variables de entorno antes de ejecutar la app:

| Variable      | Descripción                        | Ejemplo (Windows)                              |
|---------------|------------------------------------|------------------------------------------------|
| `DB_USERNAME` | Usuario de conexión en Oracle      | `HOTEL`                                        |
| `DB_PASSWORD` | Contraseña del usuario de conexión | `tuPassword`                                   |

`TNS_ADMIN` quedó como opcional: si existe, la app lo usa; si no, usa `wallet/Wallet_DondeJuanaDB`.

### 3. Grants y sinónimos en Oracle (REQUERIDO)

Las tablas `USUARIOS` y `ROLES` están en el esquema `ADMIN`.
La aplicación se conecta como usuario `HOTEL`. Para que `HOTEL` pueda leer esas tablas,
ejecuta esto **una sola vez** conectado como `ADMIN` (o un DBA):

```sql
-- Dar permisos de lectura a HOTEL
GRANT SELECT ON ADMIN.USUARIOS TO HOTEL;
GRANT SELECT ON ADMIN.ROLES    TO HOTEL;

-- Crear sinónimos en HOTEL (opcional, si se quita default_schema de application.properties)
-- CREATE OR REPLACE SYNONYM HOTEL.USUARIOS FOR ADMIN.USUARIOS;
-- CREATE OR REPLACE SYNONYM HOTEL.ROLES    FOR ADMIN.ROLES;
```

> **Nota:** La propiedad `spring.jpa.properties.hibernate.default_schema=ADMIN` en
> `application.properties` hace que Hibernate prefije todas las consultas con `ADMIN.`,
> por lo que los sinónimos son opcionales si los grants ya existen.

## Ejecutar

### Linux / Mac

```bash
export DB_USERNAME=HOTEL
export DB_PASSWORD=tuPassword
./mvnw spring-boot:run
```

### Windows (cmd)

```cmd
set DB_USERNAME=HOTEL
set DB_PASSWORD=tuPassword
.\mvnw.cmd spring-boot:run
```

### Windows (PowerShell)

```powershell
$env:DB_USERNAME="HOTEL"
$env:DB_PASSWORD="tuPassword"
.\mvnw.cmd spring-boot:run
```

Luego abrir en el navegador: `http://localhost:8080/`

## Autenticación

El sistema usa login con usuarios almacenados en la tabla `ADMIN.USUARIOS` (BCrypt).
- Usuario activo (`ACTIVO = 'S'`) con rol activo (`ROLES.ACTIVO = 'S'`) puede iniciar sesión.
- Usuario o rol inactivo (`ACTIVO = 'N'`) no puede autenticarse.

La página principal (`/`) muestra el estado de la conexión a BD:
- ✅ **DB Status: OK** — la app se conectó correctamente y puede leer `ADMIN.USUARIOS`.
- ❌ **DB Status: ERROR** — problema de conexión o permisos; se muestra el mensaje de error.

## Estructura del proyecto

```
src/
└── main/
    ├── java/com/project/hotel/
    │   ├── MvcpersonasApplication.java
    │   ├── config/
    │   │   └── SecurityConfig.java          ← Spring Security + DaoAuthenticationProvider
    │   ├── controller/
    │   │   ├── AuthController.java           ← GET /login
    │   │   ├── IndexController.java          ← GET / (con DB health check)
    │   │   ├── HabitacionController.java
    │   │   ├── PersonaController.java
    │   │   ├── ReservaController.java
    │   │   └── TipoHabitacionController.java
    │   ├── entities/
    │   │   ├── Usuario.java                  ← Mapea ADMIN.USUARIOS
    │   │   ├── Rol.java                      ← Mapea ADMIN.ROLES
    │   │   ├── Habitacion.java
    │   │   ├── Persona.java
    │   │   ├── Reserva.java
    │   │   └── TipoHabitacion.java
    │   ├── repository/
    │   └── service/
    │       └── DbUserDetailsService.java     ← UserDetailsService basado en BD
    └── resources/
        ├── application.properties
        └── templates/
            ├── auth/login.html
            └── index.html
```
