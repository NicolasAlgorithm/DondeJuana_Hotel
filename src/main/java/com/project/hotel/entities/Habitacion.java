package com.project.hotel.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Entity
@Table(name = "HABITACIONES")
public class Habitacion {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "habitacion_seq")
    @SequenceGenerator(name = "habitacion_seq", sequenceName = "SEQ_HABITACIONES", allocationSize = 1)
    @Column(name = "ID_HABITACION")
    private Long idHabitacion;

    @NotBlank
    @Column(name = "CODIGO", nullable = false, length = 30, unique = true)
    private String codigo;

    @NotBlank
    @Column(name = "NUMERO", nullable = false, length = 30)
    private String numero;

    @Column(name = "PISO")
    private Integer piso;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_TIPO_HABITACION", nullable = false)
    private TipoHabitacion tipoHabitacion;

    @NotNull
    @Column(name = "TARIFA_NOCHE", nullable = false, precision = 12, scale = 2)
    private BigDecimal tarifaNoche;

    @NotBlank
    @Column(name = "ESTADO", nullable = false, length = 20)
    private String estado;

    @NotBlank
    @Column(name = "ACTIVO", nullable = false, length = 1)
    private String activo;

    public Habitacion() {
    }

    public Long getIdHabitacion() {
        return idHabitacion;
    }

    public void setIdHabitacion(Long idHabitacion) {
        this.idHabitacion = idHabitacion;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Integer getPiso() {
        return piso;
    }

    public void setPiso(Integer piso) {
        this.piso = piso;
    }

    public TipoHabitacion getTipoHabitacion() {
        return tipoHabitacion;
    }

    public void setTipoHabitacion(TipoHabitacion tipoHabitacion) {
        this.tipoHabitacion = tipoHabitacion;
    }

    public Long getIdTipoHabitacion() {
        return tipoHabitacion != null ? tipoHabitacion.getIdTipo() : null;
    }

    public void setIdTipoHabitacion(Long idTipoHabitacion) {
        if (idTipoHabitacion == null) {
            this.tipoHabitacion = null;
            return;
        }
        TipoHabitacion t = new TipoHabitacion();
        t.setIdTipo(idTipoHabitacion);
        this.tipoHabitacion = t;
    }

    public BigDecimal getTarifaNoche() {
        return tarifaNoche;
    }

    public void setTarifaNoche(BigDecimal tarifaNoche) {
        this.tarifaNoche = tarifaNoche;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getActivo() {
        return activo;
    }

    public void setActivo(String activo) {
        this.activo = activo;
    }

    public String getDescripcion() {
        return codigo;
    }

    public void setDescripcion(String descripcion) {
        this.codigo = descripcion;
    }
}