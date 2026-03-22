-- ============================================================
-- Grants y sinónimos para que el usuario HOTEL acceda a los
-- objetos del esquema ADMIN.
--
-- PARTE 1: Ejecutar conectado como ADMIN (o un DBA).
-- PARTE 2: Ejecutar conectado como HOTEL.
--
-- Resultado:
--   * HOTEL puede hacer SELECT/INSERT/UPDATE/DELETE en todas
--     las tablas de la aplicación.
--   * HOTEL tiene sinónimos privados que apuntan a las tablas
--     y secuencias de ADMIN, por lo que las consultas funcionan
--     sin el prefijo "ADMIN." (por ejemplo: SELECT * FROM USUARIOS).
--   * Una vez ejecutados los sinónimos puedes eliminar la propiedad
--     "spring.jpa.properties.hibernate.default_schema=ADMIN" de
--     application.properties si lo prefieres; la app seguirá
--     funcionando gracias a los sinónimos.
-- ============================================================


-- ============================================================
-- PARTE 1 — Ejecutar como ADMIN
-- ============================================================

-- ------------------------------------------------------------
-- 1. PRIVILEGIOS CRUD SOBRE TABLAS
-- ------------------------------------------------------------

-- Tablas de seguridad / autenticación
GRANT SELECT, INSERT, UPDATE, DELETE ON ADMIN.USUARIOS       TO HOTEL;
GRANT SELECT, INSERT, UPDATE, DELETE ON ADMIN.ROLES          TO HOTEL;

-- Tablas del modelo de negocio
GRANT SELECT, INSERT, UPDATE, DELETE ON ADMIN.PERSONA        TO HOTEL;
GRANT SELECT, INSERT, UPDATE, DELETE ON ADMIN.TIPO_HABITACION TO HOTEL;
GRANT SELECT, INSERT, UPDATE, DELETE ON ADMIN.HABITACION     TO HOTEL;
GRANT SELECT, INSERT, UPDATE, DELETE ON ADMIN.RESERVA        TO HOTEL;


-- ------------------------------------------------------------
-- 2. PRIVILEGIOS SOBRE SECUENCIAS (necesarios para INSERT con
--    GenerationType.SEQUENCE de Hibernate)
-- ------------------------------------------------------------

GRANT SELECT ON ADMIN.SEQ_PERSONA          TO HOTEL;
GRANT SELECT ON ADMIN.SEQ_TIPO_HABITACION  TO HOTEL;
GRANT SELECT ON ADMIN.SEQ_HABITACION       TO HOTEL;
GRANT SELECT ON ADMIN.SEQ_RESERVA          TO HOTEL;


-- ============================================================
-- PARTE 2 — Ejecutar como HOTEL
-- Crea sinónimos privados en el propio esquema HOTEL sin
-- necesitar el privilegio CREATE ANY SYNONYM en ADMIN.
-- ============================================================

-- ------------------------------------------------------------
-- 3. SINÓNIMOS PRIVADOS PARA TABLAS
-- ------------------------------------------------------------

CREATE OR REPLACE SYNONYM USUARIOS        FOR ADMIN.USUARIOS;
CREATE OR REPLACE SYNONYM ROLES           FOR ADMIN.ROLES;
CREATE OR REPLACE SYNONYM PERSONA         FOR ADMIN.PERSONA;
CREATE OR REPLACE SYNONYM TIPO_HABITACION FOR ADMIN.TIPO_HABITACION;
CREATE OR REPLACE SYNONYM HABITACION      FOR ADMIN.HABITACION;
CREATE OR REPLACE SYNONYM RESERVA         FOR ADMIN.RESERVA;

-- ------------------------------------------------------------
-- 4. SINÓNIMOS PRIVADOS PARA SECUENCIAS
-- ------------------------------------------------------------

CREATE OR REPLACE SYNONYM SEQ_PERSONA         FOR ADMIN.SEQ_PERSONA;
CREATE OR REPLACE SYNONYM SEQ_TIPO_HABITACION FOR ADMIN.SEQ_TIPO_HABITACION;
CREATE OR REPLACE SYNONYM SEQ_HABITACION      FOR ADMIN.SEQ_HABITACION;
CREATE OR REPLACE SYNONYM SEQ_RESERVA         FOR ADMIN.SEQ_RESERVA;
