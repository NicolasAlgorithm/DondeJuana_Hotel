package com.project.hotel.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "HABITACION")
public class Habitacion {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "habitacion_seq")
    @SequenceGenerator(name = "habitacion_seq", sequenceName = "SEQ_HABITACION", allocationSize = 1)
    @Column(name = "ID_HABITACION")
    private Long idHabitacion;

    @NotBlank
    @Column(name = "NUMERO", nullable = false, length = 10, unique = true)
    private String numero;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_TIPO", nullable = false)
    private TipoHabitacion tipoHabitacion;

    @Column(name = "ESTADO", length = 20)
    private String estado;

    @Column(name = "DESCRIPCION", length = 255)
    private String descripcion;

    public Habitacion() {
    }

    public Habitacion(String numero, TipoHabitacion tipoHabitacion, String estado, String descripcion) {
        this.numero = numero;
        this.tipoHabitacion = tipoHabitacion;
        this.estado = estado;
        this.descripcion = descripcion;
    }

    public Long getIdHabitacion() {
        return idHabitacion;
    }

    public void setIdHabitacion(Long idHabitacion) {
        this.idHabitacion = idHabitacion;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public TipoHabitacion getTipoHabitacion() {
        return tipoHabitacion;
    }

    public void setTipoHabitacion(TipoHabitacion tipoHabitacion) {
        this.tipoHabitacion = tipoHabitacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
