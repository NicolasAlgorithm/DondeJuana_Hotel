package com.project.hotel.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * Respuesta completa del endpoint de calendario de reservas.
 *
 * <p>Contiene el rango de fechas solicitado y la lista de habitaciones
 * activas con su estado día a día dentro de ese rango.</p>
 *
 * <p>Ejemplo de JSON producido:
 * <pre>{@code
 * {
 *   "fechaInicio": "2026-04-01",
 *   "fechaFin": "2026-04-08",
 *   "totalDias": 7,
 *   "totalHabitaciones": 12,
 *   "habitaciones": [ ... ]
 * }
 * }</pre>
 * </p>
 */
public class CalendarioResponse {

    /** Primer día del rango (incluido). */
    private LocalDate fechaInicio;

    /** Último día del rango (excluido, mismo criterio que las reservas). */
    private LocalDate fechaFin;

    /** Número de días en el rango ({@code fechaFin - fechaInicio}). */
    private int totalDias;

    /** Número de habitaciones incluidas en la respuesta. */
    private int totalHabitaciones;

    /** Lista de habitaciones con su estado por día. */
    private List<HabitacionCalendarioDTO> habitaciones;

    public CalendarioResponse() {
    }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public int getTotalDias() { return totalDias; }
    public void setTotalDias(int totalDias) { this.totalDias = totalDias; }

    public int getTotalHabitaciones() { return totalHabitaciones; }
    public void setTotalHabitaciones(int totalHabitaciones) { this.totalHabitaciones = totalHabitaciones; }

    public List<HabitacionCalendarioDTO> getHabitaciones() { return habitaciones; }
    public void setHabitaciones(List<HabitacionCalendarioDTO> habitaciones) { this.habitaciones = habitaciones; }
}
