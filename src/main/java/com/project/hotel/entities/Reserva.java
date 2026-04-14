package com.project.hotel.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "RESERVAS")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reserva_seq")
    @SequenceGenerator(name = "reserva_seq", sequenceName = "SEQ_RESERVAS", allocationSize = 1)
    @Column(name = "ID_RESERVA")
    private Long idReserva;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_HUESPED", nullable = false)
    private Persona persona;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_HABITACION", nullable = false)
    private Habitacion habitacion;

    @NotNull
    @Column(name = "FECHA_INICIO", nullable = false)
    private LocalDate fechaEntrada;

    @NotNull
    @Column(name = "FECHA_FIN", nullable = false)
    private LocalDate fechaSalida;

    @Column(name = "ESTADO", length = 20)
    private String estado;

    @Column(name = "FECHA_HORA_CHECKIN")
    private LocalDateTime fechaHoraCheckIn;

    @Column(name = "FECHA_SALIDA_REAL")
    private LocalDate fechaSalidaReal;

    // La tabla RESERVAS no tiene columna TOTAL; se deja solo para no romper vistas/formularios.
    @Transient
    private BigDecimal total;

    public Reserva() {
    }

    public Reserva(Persona persona, Habitacion habitacion, LocalDate fechaEntrada, LocalDate fechaSalida, String estado, BigDecimal total) {
        this.persona = persona;
        this.habitacion = habitacion;
        this.fechaEntrada = fechaEntrada;
        this.fechaSalida = fechaSalida;
        this.estado = estado;
        this.total = total;
    }

    public Long getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(Long idReserva) {
        this.idReserva = idReserva;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    public Habitacion getHabitacion() {
        return habitacion;
    }

    public void setHabitacion(Habitacion habitacion) {
        this.habitacion = habitacion;
    }

    public LocalDate getFechaEntrada() {
        return fechaEntrada;
    }

    public void setFechaEntrada(LocalDate fechaEntrada) {
        this.fechaEntrada = fechaEntrada;
    }

    public LocalDate getFechaSalida() {
        return fechaSalida;
    }

    public void setFechaSalida(LocalDate fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public LocalDateTime getFechaHoraCheckIn() {
        return fechaHoraCheckIn;
    }

    public void setFechaHoraCheckIn(LocalDateTime fechaHoraCheckIn) {
        this.fechaHoraCheckIn = fechaHoraCheckIn;
    }

    public LocalDate getFechaSalidaReal() {
        return fechaSalidaReal;
    }

    public void setFechaSalidaReal(LocalDate fechaSalidaReal) {
        this.fechaSalidaReal = fechaSalidaReal;
    }
}
