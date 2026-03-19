package com.project.hotel.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Entity
@Table(name = "HABITACIONES")
public class Habitacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_HABITACION")
    private Long idHabitacion;

    @NotBlank
    @Column(name = "CODIGO", nullable = false, unique = true, length = 20)
    private String codigo;

    @NotBlank
    @Column(name = "NUMERO", nullable = false, length = 10)
    private String numero;

    @Column(name = "PISO")
    private Integer piso;

    @NotNull
    @Column(name = "ID_TIPO_HABITACION", nullable = false)
    private Long idTipoHabitacion;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Column(name = "TARIFA_NOCHE", nullable = false, precision = 12, scale = 2)
    private BigDecimal tarifaNoche;

    @NotBlank
    @Column(name = "ESTADO", nullable = false, length = 20)
    private String estado; // DISPONIBLE | OCUPADA | MANTENIMIENTO

    @Column(name = "ACTIVO", nullable = false, length = 1)
    private String activo = "S";

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

    public BigDecimal getTarifaNoche() { return tarifaNoche; }
    public void setTarifaNoche(BigDecimal tarifaNoche) { this.tarifaNoche = tarifaNoche; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getActivo() { return activo; }
    public void setActivo(String activo) { this.activo = activo; }
}