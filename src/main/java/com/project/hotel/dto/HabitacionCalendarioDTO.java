package com.project.hotel.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Datos de una habitación junto con su estado por cada día del rango
 * solicitado al endpoint de calendario.
 *
 * <p>Ejemplo de JSON producido:
 * <pre>{@code
 * {
 *   "idHabitacion": 5,
 *   "codigo": "HAB-105",
 *   "numero": "105",
 *   "piso": 1,
 *   "idTipoHabitacion": 2,
 *   "nombreTipoHabitacion": "Suite",
 *   "tarifaNoche": 150.00,
 *   "dias": [ ... ]
 * }
 * }</pre>
 * </p>
 */
public class HabitacionCalendarioDTO {

    private Long idHabitacion;
    private String codigo;
    private String numero;
    private Integer piso;
    private Long idTipoHabitacion;
    private String nombreTipoHabitacion;
    private BigDecimal tarifaNoche;

    /** Estado por cada día del rango solicitado (ordenado cronológicamente). */
    private List<DiaEstadoDTO> dias;

    public HabitacionCalendarioDTO() {
    }

    public Long getIdHabitacion() { return idHabitacion; }
    public void setIdHabitacion(Long idHabitacion) { this.idHabitacion = idHabitacion; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public Integer getPiso() { return piso; }
    public void setPiso(Integer piso) { this.piso = piso; }

    public Long getIdTipoHabitacion() { return idTipoHabitacion; }
    public void setIdTipoHabitacion(Long idTipoHabitacion) { this.idTipoHabitacion = idTipoHabitacion; }

    public String getNombreTipoHabitacion() { return nombreTipoHabitacion; }
    public void setNombreTipoHabitacion(String nombreTipoHabitacion) { this.nombreTipoHabitacion = nombreTipoHabitacion; }

    public BigDecimal getTarifaNoche() { return tarifaNoche; }
    public void setTarifaNoche(BigDecimal tarifaNoche) { this.tarifaNoche = tarifaNoche; }

    public List<DiaEstadoDTO> getDias() { return dias; }
    public void setDias(List<DiaEstadoDTO> dias) { this.dias = dias; }
}
