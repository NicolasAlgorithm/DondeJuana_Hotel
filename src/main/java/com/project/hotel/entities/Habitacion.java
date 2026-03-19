package com.project.hotel.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
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

    // ====== Compatibilidad con service previo ======
    // Si el service usa request.getIdTipoHabitacion(), este setter evita error.
    public void setIdTipoHabitacion(Long idTipoHabitacion) {
        if (idTipoHabitacion == null) {
            this.tipoHabitacion = null;
        } else {
            TipoHabitacion t = new TipoHabitacion();
            t.setIdTipo(idTipoHabitacion); // ajusta si en tu entidad TipoHabitacion el ID se llama distinto
            this.tipoHabitacion = t;
        }
    }

    public Long getIdTipoHabitacion() {
        return (tipoHabitacion != null) ? tipoHabitacion.getIdTipo() : null; // ajusta si el getter del ID es distinto
    }

    // Alias de descripción por compatibilidad si en DTO/service usas "codigo"
    public String getCodigo() {
        return this.descripcion;
    }

    public void setCodigo(String codigo) {
        this.descripcion = codigo;
    }

    // Alias por compatibilidad si en DTO/service usas "piso"
    public Integer getPiso() {
        return null;
    }

    public void setPiso(Integer piso) {
        // No-op: la entidad actual no tiene columna piso
    }

    // Alias por compatibilidad si en DTO/service usas "tarifaNoche"
    public java.math.BigDecimal getTarifaNoche() {
        return null;
    }

    public void setTarifaNoche(java.math.BigDecimal tarifaNoche) {
        // No-op: la entidad actual no tiene columna tarifa en esta versión
    }

    // Alias por compatibilidad si en DTO/service usas "activo"
    public String getActivo() {
        return "S";
    }

    public void setActivo(String activo) {
        // No-op: la entidad actual no tiene columna activo
    }
}