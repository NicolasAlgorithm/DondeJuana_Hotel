-- ============================================================
-- Migración: agregar columna TOTAL a la tabla RESERVAS
-- Ejecutar como ADMIN (propietario del esquema) en Oracle.
-- ============================================================
ALTER TABLE ADMIN.RESERVAS ADD (TOTAL NUMBER(12,2));
