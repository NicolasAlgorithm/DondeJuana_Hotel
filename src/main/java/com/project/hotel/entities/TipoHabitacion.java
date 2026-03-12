package com.project.hotel.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "TIPO_HABITACION")
public class TipoHabitacion {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tipo_hab_seq")
    @SequenceGenerator(name = "tipo_hab_seq", sequenceName = "SEQ_TIPO_HABITACION", allocationSize = 1)
    @Column(name = "ID_TIPO")
    private Long idTipo;

    @NotBlank
    @Column(name = "NOMBRE", nullable = false, length = 100)
    private String nombre;

    @Column(name = "DESCRIPCION", length = 255)
    private String descripcion;

    @NotNull
    @Column(name = "PRECIO_NOCHE", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioNoche;

    @OneToMany(mappedBy = "tipoHabitacion", fetch = FetchType.LAZY)
    private List<Habitacion> habitaciones;

    public TipoHabitacion() {
    }

    public TipoHabitacion(String nombre, String descripcion, BigDecimal precioNoche) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precioNoche = precioNoche;
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPrecioNoche() {
        return precioNoche;
    }

    public void setPrecioNoche(BigDecimal precioNoche) {
        this.precioNoche = precioNoche;
    }

    public List<Habitacion> getHabitaciones() {
        return habitaciones;
    }

    public void setHabitaciones(List<Habitacion> habitaciones) {
        this.habitaciones = habitaciones;
    }
}
