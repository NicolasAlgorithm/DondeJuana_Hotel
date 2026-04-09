package com.project.hotel.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class HabitacionRequest {

    @NotBlank
    private String codigo;

    @NotBlank
    private String numero;

    private Integer piso;

    @NotNull
    private Long idTipoHabitacion;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal tarifaNoche;

    @NotBlank
    private String estado; // DISPONIBLE | RESERVADA | OCUPADA | MANTENIMIENTO

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public Integer getPiso() { return piso; }
    public void setPiso(Integer piso) { this.piso = piso; }

    public Long getIdTipoHabitacion() { return idTipoHabitacion; }
    public void setIdTipoHabitacion(Long idTipoHabitacion) { this.idTipoHabitacion = idTipoHabitacion; }

    public BigDecimal getTarifaNoche() { return tarifaNoche; }
    public void setTarifaNoche(BigDecimal tarifaNoche) { this.tarifaNoche = tarifaNoche; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}