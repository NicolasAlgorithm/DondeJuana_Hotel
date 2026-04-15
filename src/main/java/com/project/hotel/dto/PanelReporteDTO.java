package com.project.hotel.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PanelReporteDTO {

    private final String periodo;
    private final LocalDate fechaInicio;
    private final LocalDate fechaFin;
    private final int totalHabitaciones;
    private final long nochesDisponibles;
    private final long nochesOcupadas;
    private final BigDecimal ocupacionPorcentaje;
    private final BigDecimal ingresos;
    private final int reservasEnPeriodo;

    public PanelReporteDTO(
            String periodo,
            LocalDate fechaInicio,
            LocalDate fechaFin,
            int totalHabitaciones,
            long nochesDisponibles,
            long nochesOcupadas,
            BigDecimal ocupacionPorcentaje,
            BigDecimal ingresos,
            int reservasEnPeriodo
    ) {
        this.periodo = periodo;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.totalHabitaciones = totalHabitaciones;
        this.nochesDisponibles = nochesDisponibles;
        this.nochesOcupadas = nochesOcupadas;
        this.ocupacionPorcentaje = ocupacionPorcentaje;
        this.ingresos = ingresos;
        this.reservasEnPeriodo = reservasEnPeriodo;
    }

    public String getPeriodo() {
        return periodo;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public int getTotalHabitaciones() {
        return totalHabitaciones;
    }

    public long getNochesDisponibles() {
        return nochesDisponibles;
    }

    public long getNochesOcupadas() {
        return nochesOcupadas;
    }

    public BigDecimal getOcupacionPorcentaje() {
        return ocupacionPorcentaje;
    }

    public BigDecimal getIngresos() {
        return ingresos;
    }

    public int getReservasEnPeriodo() {
        return reservasEnPeriodo;
    }

    public boolean isSinDatos() {
        return reservasEnPeriodo == 0;
    }
}
