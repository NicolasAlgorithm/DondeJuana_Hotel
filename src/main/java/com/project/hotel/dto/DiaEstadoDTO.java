package com.project.hotel.dto;

import java.time.LocalDate;

/**
 * Estado de una habitación para un día concreto dentro del calendario.
 *
 * <p>Ejemplo de JSON producido:
 * <pre>{@code
 * {
 *   "fecha": "2026-04-15",
 *   "estado": "OCUPADA",
 *   "etiquetaEstado": "Ocupada",
 *   "codigoColor": "#dc3545",
 *   "idReserva": 42
 * }
 * }</pre>
 * </p>
 */
public class DiaEstadoDTO {

    /** Fecha del día (ISO-8601, yyyy-MM-dd). */
    private LocalDate fecha;

    /** Estado del día (enum con color incluido). */
    private EstadoCalendario estado;

    /** Etiqueta legible del estado, conveniente para frontends. */
    private String etiquetaEstado;

    /** Código de color hexadecimal CSS sugerido. */
    private String codigoColor;

    /**
     * ID de la reserva que ocupa este día, o {@code null} si la habitación
     * no está ocupada.
     */
    private Long idReserva;

    public DiaEstadoDTO() {
    }

    /**
     * Constructor principal.
     *
     * @param fecha     día representado
     * @param estado    estado calculado para ese día
     * @param idReserva id de la reserva si está ocupada, o {@code null}
     */
    public DiaEstadoDTO(LocalDate fecha, EstadoCalendario estado, Long idReserva) {
        this.fecha = fecha;
        this.estado = estado;
        this.etiquetaEstado = estado.getEtiqueta();
        this.codigoColor = estado.getCodigoColor();
        this.idReserva = idReserva;
    }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public EstadoCalendario getEstado() { return estado; }
    public void setEstado(EstadoCalendario estado) { this.estado = estado; }

    public String getEtiquetaEstado() { return etiquetaEstado; }
    public void setEtiquetaEstado(String etiquetaEstado) { this.etiquetaEstado = etiquetaEstado; }

    public String getCodigoColor() { return codigoColor; }
    public void setCodigoColor(String codigoColor) { this.codigoColor = codigoColor; }

    public Long getIdReserva() { return idReserva; }
    public void setIdReserva(Long idReserva) { this.idReserva = idReserva; }
}
