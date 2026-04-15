package com.project.hotel.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class TipoHabitacionRequest {

    @NotBlank
    @Size(min = 2, max = 100)
    @Pattern(regexp = "^[\\p{L}0-9 .,'-]+$", message = "Nombre con formato invalido")
    private String nombre;

    @Size(max = 255)
    @Pattern(regexp = "^[\\p{L}0-9 .,'-]*$", message = "Descripcion con formato invalido")
    private String descripcion;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal tarifaBase;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public BigDecimal getTarifaBase() { return tarifaBase; }
    public void setTarifaBase(BigDecimal tarifaBase) { this.tarifaBase = tarifaBase; }
}
