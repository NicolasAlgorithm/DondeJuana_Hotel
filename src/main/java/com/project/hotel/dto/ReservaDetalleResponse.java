package com.project.hotel.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ReservaDetalleResponse {

    private Long idReserva;
    private Long idPersona;
    private String nombreHuesped;
    private Long idHabitacion;
    private String numeroHabitacion;
    private LocalDate fechaEntrada;
    private LocalDate fechaSalida;
    private String estado;
    private LocalDate fechaSalidaReal;
    private LocalDateTime fechaHoraCheckIn;

    public Long getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(Long idReserva) {
        this.idReserva = idReserva;
    }

    public Long getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(Long idPersona) {
        this.idPersona = idPersona;
    }

    public String getNombreHuesped() {
        return nombreHuesped;
    }

    public void setNombreHuesped(String nombreHuesped) {
        this.nombreHuesped = nombreHuesped;
    }

    public Long getIdHabitacion() {
        return idHabitacion;
    }

    public void setIdHabitacion(Long idHabitacion) {
        this.idHabitacion = idHabitacion;
    }

    public String getNumeroHabitacion() {
        return numeroHabitacion;
    }

    public void setNumeroHabitacion(String numeroHabitacion) {
        this.numeroHabitacion = numeroHabitacion;
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

    public LocalDate getFechaSalidaReal() {
        return fechaSalidaReal;
    }

    public void setFechaSalidaReal(LocalDate fechaSalidaReal) {
        this.fechaSalidaReal = fechaSalidaReal;
    }

    public LocalDateTime getFechaHoraCheckIn() {
        return fechaHoraCheckIn;
    }

    public void setFechaHoraCheckIn(LocalDateTime fechaHoraCheckIn) {
        this.fechaHoraCheckIn = fechaHoraCheckIn;
    }
}
