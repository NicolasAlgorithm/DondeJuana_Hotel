-- Performance indexes for reservation overlap checks under concurrent load.
-- Run as ADMIN schema owner.

CREATE INDEX IDX_RESERVAS_HAB_FECHAS
    ON RESERVAS (ID_HABITACION, FECHA_INICIO, FECHA_FIN);

CREATE INDEX IDX_RESERVAS_ESTADO
    ON RESERVAS (ESTADO);

-- Optional stats refresh after index creation.
BEGIN
    DBMS_STATS.GATHER_TABLE_STATS(ownname => 'ADMIN', tabname => 'RESERVAS');
END;
/
