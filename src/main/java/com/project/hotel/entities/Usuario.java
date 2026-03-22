package com.project.hotel.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "USUARIOS")
public class Usuario {

    @Id
    @Column(name = "ID_USUARIO")
    private Long idUsuario;

    @Column(name = "USERNAME", nullable = false)
    private String username;

    @Column(name = "PASSWORD_HASH", nullable = false)
    private String passwordHash;

    @Column(name = "ACTIVO", nullable = false)
    private String activo; // 'S' / 'N'

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_ROL", referencedColumnName = "ID_ROL", nullable = false)
    private Rol rol;

    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getActivo() { return activo; }
    public void setActivo(String activo) { this.activo = activo; }

    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }

    public boolean isActivo() {
        return "S".equalsIgnoreCase(activo);
    }
}