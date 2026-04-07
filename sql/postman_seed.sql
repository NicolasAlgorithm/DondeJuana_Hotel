-- ============================================================
-- DATOS SEMILLA MÍNIMOS PARA PRUEBAS DE ENDPOINTS POSTMAN
-- Hotel Donde Juana - Backend Spring Boot / Oracle ADB
--
-- Ejecutar conectado como ADMIN en Oracle ADB antes de
-- correr la colección Postman.
--
-- Tablas: ROLES, USUARIOS, TIPOS_HABITACION, HABITACIONES,
--         HUESPEDES, RESERVAS
--
-- Usuario de prueba: admin / admin123
-- Hash BCrypt ($2a$10$...): admin123 validado con BCryptPasswordEncoder
-- ============================================================

-- ------------------------------------------------------------
-- 1. ROLES
-- ------------------------------------------------------------
MERGE INTO ADMIN.ROLES r
USING (
    SELECT 1   AS ID_ROL, 'ADMINISTRADOR' AS NOMBRE, 'S' AS ACTIVO FROM DUAL UNION ALL
    SELECT 2   AS ID_ROL, 'RECEPCIONISTA'  AS NOMBRE, 'S' AS ACTIVO FROM DUAL
) s ON (r.ID_ROL = s.ID_ROL)
WHEN MATCHED THEN
    UPDATE SET r.NOMBRE = s.NOMBRE, r.ACTIVO = s.ACTIVO
WHEN NOT MATCHED THEN
    INSERT (ID_ROL, NOMBRE, ACTIVO)
    VALUES (s.ID_ROL, s.NOMBRE, s.ACTIVO);

-- ------------------------------------------------------------
-- 2. USUARIO ADMIN (contraseña: admin123)
--    Hash generado con BCryptPasswordEncoder (strength 10).
--    Verificar con: new BCryptPasswordEncoder().matches("admin123", <hash>)
-- ------------------------------------------------------------
MERGE INTO ADMIN.USUARIOS u
USING (
    SELECT
        1                                                        AS ID_USUARIO,
        1                                                        AS ID_ROL,
        'admin'                                                  AS USERNAME,
        '$2a$10$N4.DkAzFcLXC.Jubv1XTuuPXvHQVkuRnPJHqDqk3qHjcjQKlX4TPG' AS PASSWORD_HASH,
        'S'                                                      AS ACTIVO
    FROM DUAL
) s ON (u.USERNAME = s.USERNAME)
WHEN MATCHED THEN
    UPDATE SET
        u.ID_ROL       = s.ID_ROL,
        u.PASSWORD_HASH = s.PASSWORD_HASH,
        u.ACTIVO       = s.ACTIVO
WHEN NOT MATCHED THEN
    INSERT (ID_USUARIO, ID_ROL, USERNAME, PASSWORD_HASH, ACTIVO)
    VALUES (s.ID_USUARIO, s.ID_ROL, s.USERNAME, s.PASSWORD_HASH, s.ACTIVO);

-- ------------------------------------------------------------
-- 3. USUARIO RECEPCIONISTA (contraseña: recep123)
--    Hash BCrypt para "recep123"
-- ------------------------------------------------------------
MERGE INTO ADMIN.USUARIOS u
USING (
    SELECT
        2                                                        AS ID_USUARIO,
        2                                                        AS ID_ROL,
        'recepcion'                                              AS USERNAME,
        '$2a$10$8K1p/a0dR1xqM2LtPgZ7v.QZo6.3zXPdJmHkQdAnEXxBr5oQJeKqS' AS PASSWORD_HASH,
        'S'                                                      AS ACTIVO
    FROM DUAL
) s ON (u.USERNAME = s.USERNAME)
WHEN MATCHED THEN
    UPDATE SET
        u.ID_ROL       = s.ID_ROL,
        u.PASSWORD_HASH = s.PASSWORD_HASH,
        u.ACTIVO       = s.ACTIVO
WHEN NOT MATCHED THEN
    INSERT (ID_USUARIO, ID_ROL, USERNAME, PASSWORD_HASH, ACTIVO)
    VALUES (s.ID_USUARIO, s.ID_ROL, s.USERNAME, s.PASSWORD_HASH, s.ACTIVO);

-- ------------------------------------------------------------
-- 4. TIPOS DE HABITACIÓN
-- ------------------------------------------------------------
MERGE INTO ADMIN.TIPOS_HABITACION t
USING (
    SELECT 1 AS ID_TIPO_HABITACION, 'Estándar'    AS NOMBRE, 'Habitación estándar con cama doble'  AS DESCRIPCION, 120000.00 AS TARIFA_BASE FROM DUAL UNION ALL
    SELECT 2 AS ID_TIPO_HABITACION, 'Suite'        AS NOMBRE, 'Suite de lujo con vista al jardín'   AS DESCRIPCION, 280000.00 AS TARIFA_BASE FROM DUAL UNION ALL
    SELECT 3 AS ID_TIPO_HABITACION, 'Familiar'     AS NOMBRE, 'Habitación familiar con 2 camas'     AS DESCRIPCION, 200000.00 AS TARIFA_BASE FROM DUAL
) s ON (t.ID_TIPO_HABITACION = s.ID_TIPO_HABITACION)
WHEN MATCHED THEN
    UPDATE SET t.NOMBRE = s.NOMBRE, t.DESCRIPCION = s.DESCRIPCION, t.TARIFA_BASE = s.TARIFA_BASE
WHEN NOT MATCHED THEN
    INSERT (ID_TIPO_HABITACION, NOMBRE, DESCRIPCION, TARIFA_BASE)
    VALUES (s.ID_TIPO_HABITACION, s.NOMBRE, s.DESCRIPCION, s.TARIFA_BASE);

-- ------------------------------------------------------------
-- 5. HABITACIONES
-- Nota: actualizar SEQ_TIPOS_HABITACION y SEQ_HABITACIONES
--       si los IDs ya existen en la BD.
-- ------------------------------------------------------------
MERGE INTO ADMIN.HABITACIONES h
USING (
    SELECT 1 AS ID_HABITACION, 'HAB-101' AS CODIGO, '101' AS NUMERO, 1 AS PISO, 1 AS ID_TIPO_HABITACION, 120000.00 AS TARIFA_NOCHE, 'DISPONIBLE' AS ESTADO, 'S' AS ACTIVO FROM DUAL UNION ALL
    SELECT 2 AS ID_HABITACION, 'HAB-201' AS CODIGO, '201' AS NUMERO, 2 AS PISO, 2 AS ID_TIPO_HABITACION, 280000.00 AS TARIFA_NOCHE, 'DISPONIBLE' AS ESTADO, 'S' AS ACTIVO FROM DUAL UNION ALL
    SELECT 3 AS ID_HABITACION, 'HAB-301' AS CODIGO, '301' AS NUMERO, 3 AS PISO, 3 AS ID_TIPO_HABITACION, 200000.00 AS TARIFA_NOCHE, 'DISPONIBLE' AS ESTADO, 'S' AS ACTIVO FROM DUAL
) s ON (h.ID_HABITACION = s.ID_HABITACION)
WHEN MATCHED THEN
    UPDATE SET
        h.CODIGO            = s.CODIGO,
        h.NUMERO            = s.NUMERO,
        h.PISO              = s.PISO,
        h.ID_TIPO_HABITACION = s.ID_TIPO_HABITACION,
        h.TARIFA_NOCHE      = s.TARIFA_NOCHE,
        h.ESTADO            = s.ESTADO,
        h.ACTIVO            = s.ACTIVO
WHEN NOT MATCHED THEN
    INSERT (ID_HABITACION, CODIGO, NUMERO, PISO, ID_TIPO_HABITACION, TARIFA_NOCHE, ESTADO, ACTIVO)
    VALUES (s.ID_HABITACION, s.CODIGO, s.NUMERO, s.PISO, s.ID_TIPO_HABITACION, s.TARIFA_NOCHE, s.ESTADO, s.ACTIVO);

-- ------------------------------------------------------------
-- 6. HUÉSPEDES (PERSONAS)
-- ------------------------------------------------------------
MERGE INTO ADMIN.HUESPEDES h
USING (
    SELECT
        1           AS ID_HUESPED,
        'María'     AS NOMBRES,
        'García'    AS APELLIDOS,
        'CC'        AS TIPO_DOCUMENTO,
        '10001TEST' AS NUMERO_DOCUMENTO,
        'maria.garcia@test.com' AS EMAIL,
        '3001000001' AS TELEFONO
    FROM DUAL UNION ALL
    SELECT
        2           AS ID_HUESPED,
        'Carlos'    AS NOMBRES,
        'López'     AS APELLIDOS,
        'CC'        AS TIPO_DOCUMENTO,
        '10002TEST' AS NUMERO_DOCUMENTO,
        'carlos.lopez@test.com' AS EMAIL,
        '3002000002' AS TELEFONO
    FROM DUAL
) s ON (h.ID_HUESPED = s.ID_HUESPED)
WHEN MATCHED THEN
    UPDATE SET
        h.NOMBRES          = s.NOMBRES,
        h.APELLIDOS        = s.APELLIDOS,
        h.TIPO_DOCUMENTO   = s.TIPO_DOCUMENTO,
        h.NUMERO_DOCUMENTO = s.NUMERO_DOCUMENTO,
        h.EMAIL            = s.EMAIL,
        h.TELEFONO         = s.TELEFONO
WHEN NOT MATCHED THEN
    INSERT (ID_HUESPED, NOMBRES, APELLIDOS, TIPO_DOCUMENTO, NUMERO_DOCUMENTO, EMAIL, TELEFONO)
    VALUES (s.ID_HUESPED, s.NOMBRES, s.APELLIDOS, s.TIPO_DOCUMENTO, s.NUMERO_DOCUMENTO, s.EMAIL, s.TELEFONO);

-- ------------------------------------------------------------
-- 7. AJUSTAR SECUENCIAS para evitar conflicto con IDs insertados
--    (ejecutar solo si las secuencias no han avanzado)
-- ------------------------------------------------------------
-- ALTER SEQUENCE ADMIN.SEQ_TIPOS_HABITACION RESTART START WITH 10;
-- ALTER SEQUENCE ADMIN.SEQ_HABITACIONES    RESTART START WITH 10;
-- ALTER SEQUENCE ADMIN.SEQ_HUESPEDES       RESTART START WITH 10;
-- ALTER SEQUENCE ADMIN.SEQ_RESERVAS        RESTART START WITH 10;

COMMIT;

-- ============================================================
-- VERIFICACIÓN (ejecutar para confirmar seed)
-- ============================================================
-- SELECT 'ROLES'           AS TABLA, COUNT(*) AS FILAS FROM ADMIN.ROLES           UNION ALL
-- SELECT 'USUARIOS'        AS TABLA, COUNT(*) AS FILAS FROM ADMIN.USUARIOS        UNION ALL
-- SELECT 'TIPOS_HABITACION' AS TABLA, COUNT(*) AS FILAS FROM ADMIN.TIPOS_HABITACION UNION ALL
-- SELECT 'HABITACIONES'    AS TABLA, COUNT(*) AS FILAS FROM ADMIN.HABITACIONES    UNION ALL
-- SELECT 'HUESPEDES'       AS TABLA, COUNT(*) AS FILAS FROM ADMIN.HUESPEDES;
