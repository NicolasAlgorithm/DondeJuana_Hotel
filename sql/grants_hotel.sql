-- ============================================================
-- Grants necesarios para que el usuario HOTEL acceda a las
-- tablas del esquema ADMIN.
-- Ejecutar conectado como ADMIN (o un DBA).
-- ============================================================

-- Permisos de lectura
GRANT SELECT ON ADMIN.USUARIOS TO HOTEL;
GRANT SELECT ON ADMIN.ROLES    TO HOTEL;

-- (Opcional) Sinónimos privados en HOTEL
-- Necesarios solo si se elimina "hibernate.default_schema=ADMIN"
-- de application.properties.
-- CREATE OR REPLACE SYNONYM HOTEL.USUARIOS FOR ADMIN.USUARIOS;
-- CREATE OR REPLACE SYNONYM HOTEL.ROLES    FOR ADMIN.ROLES;
