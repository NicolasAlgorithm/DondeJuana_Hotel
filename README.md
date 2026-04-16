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

### 2. Variables de entorno (opcionales)

La app ya incluye valores por defecto para conexión (usuario y contraseña) y puede iniciar sin exportar variables.
Si quieres sobrescribirlos en otro ambiente, usa estas variables:

| Variable      | Descripción                        | Ejemplo (Windows)                              |
|---------------|------------------------------------|------------------------------------------------|
| `DB_USERNAME` | Usuario de conexión en Oracle      | `HOTEL`                                        |
| `DB_PASSWORD` | Contraseña del usuario de conexión | `tuPassword`                                   |
| `APP_TEMPLATES_EXTERNAL_PREFIX` | Ruta de plantillas externas | `file:./` o `file:/opt/app/` |

`TNS_ADMIN` quedó como opcional: si existe, la app lo usa; si no, usa `wallet/Wallet_DondeJuanaDB`.

La vista principal se carga desde `index.html` en la raíz del proyecto mediante Thymeleaf.

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

## Autenticación

El sistema usa login con usuarios almacenados en la tabla `ADMIN.USUARIOS` (BCrypt).
- Usuario activo (`ACTIVO = 'S'`) con rol activo (`ROLES.ACTIVO = 'S'`) puede iniciar sesión.
- Usuario o rol inactivo (`ACTIVO = 'N'`) no puede autenticarse.

La página principal (`/`) muestra el estado de la conexión a BD:
- ✅ **DB Status: OK** — la app se conectó correctamente y puede leer `ADMIN.USUARIOS`.
- ❌ **DB Status: ERROR** — problema de conexión o permisos; se muestra el mensaje de error.

## Pruebas unitarias

El repositorio usa **JUnit 5 + Mockito** con `spring-boot-starter-test`.

### Ejecutar pruebas

```bash
./mvnw test
```

### Alcance cubierto (suite actual)

- **Backend (servicios críticos):**
  - `ReservaService` *(pruebas existentes en el repositorio)*
  - `CalendarioService` *(pruebas existentes en el repositorio)*
  - `HabitacionServiceImpl` *(nuevo en esta PR)*
  - `DbUserDetailsService` *(nuevo en esta PR, autenticación y permisos)*
- **Frontend MVC (flujo UI en controladores):**
  - `ReservaController` *(pruebas existentes en el repositorio)*
  - `CalendarioController` *(pruebas existentes en el repositorio)*
  - `HabitacionMvcController` *(nuevo en esta PR)*
  - `IndexController` *(nuevo en esta PR)*

## Pruebas E2E (integración)

Se agregó la suite `E2EFlujosIntegracionTest` para validar de extremo a extremo:

- Login (éxito, error y sesión)
- Reserva (creación y consulta)
- Check-in / Check-out (transición completa de estado)
- Pago (validación de monto total al completar el flujo)
- Reportes (ocupación en calendario)

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
        └── auth/login.html

  index.html                                  ← Vista principal en la raíz del proyecto
```
