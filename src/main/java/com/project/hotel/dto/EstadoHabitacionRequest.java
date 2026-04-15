package com.project.hotel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class EstadoHabitacionRequest {

    @NotBlank
    @Pattern(regexp = "^(DISPONIBLE|RESERVADA|OCUPADA|MANTENIMIENTO)$", message = "Estado invalido")
    private String estado;

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}