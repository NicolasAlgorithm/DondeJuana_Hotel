package com.project.hotel.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "TIPOS_HABITACION")
public class TipoHabitacion {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tipos_hab_seq")
    @SequenceGenerator(name = "tipos_hab_seq", sequenceName = "SEQ_TIPOS_HABITACION", allocationSize = 1)
    @Column(name = "ID_TIPO_HABITACION")
    private Long idTipo;

    @NotBlank
    @Column(name = "NOMBRE", nullable = false, length = 100)
    private String nombre;

    @NotNull
    @Column(name = "CAPACIDAD", nullable = false)
    private Integer capacidad;

    @NotNull
    @Column(name = "TARIFA_BASE", nullable = false, precision = 12, scale = 2)
    private BigDecimal tarifaBase;

    @Column(name = "DESCRIPCION", length = 255)
    private String descripcion;

    @NotBlank
    @Column(name = "ACTIVO", nullable = false, length = 1)
    private String activo;

    @OneToMany(mappedBy = "tipoHabitacion", fetch = FetchType.LAZY)
    private List<Habitacion> habitaciones;

    public TipoHabitacion() {
    }

    public Long getIdTipo() {
        return idTipo;
    }

    public void setIdTipo(Long idTipo) {
        this.idTipo = idTipo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(Integer capacidad) {
        this.capacidad = capacidad;
    }

    public BigDecimal getTarifaBase() {
        return tarifaBase;
    }

    public void setTarifaBase(BigDecimal tarifaBase) {
        this.tarifaBase = tarifaBase;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getActivo() {
        return activo;
    }

    public void setActivo(String activo) {
        this.activo = activo;
    }

    public List<Habitacion> getHabitaciones() {
        return habitaciones;
    }

    public void setHabitaciones(List<Habitacion> habitaciones) {
        this.habitaciones = habitaciones;
    }

    // Compatibilidad con código existente que usa "precioNoche"
    public BigDecimal getPrecioNoche() {
        return tarifaBase;
    }

    public void setPrecioNoche(BigDecimal precioNoche) {
        this.tarifaBase = precioNoche;
    }
}
