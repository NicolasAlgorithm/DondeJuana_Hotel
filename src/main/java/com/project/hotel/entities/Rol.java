package com.project.hotel.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ROLES")
public class Rol {

    @Id
    @Column(name = "ID_ROL")
    private Long idRol;

    @Column(name = "NOMBRE", nullable = false)
    private String nombre;

    @Column(name = "ACTIVO", nullable = false)
    private String activo; // 'S' / 'N'

    public Long getIdRol() { return idRol; }
    public void setIdRol(Long idRol) { this.idRol = idRol; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getActivo() { return activo; }
    public void setActivo(String activo) { this.activo = activo; }

    public boolean isActivo() {
        return "S".equalsIgnoreCase(activo);
    }
}