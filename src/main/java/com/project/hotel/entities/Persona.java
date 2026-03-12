package com.project.hotel.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "PERSONA")
public class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "persona_seq")
    @SequenceGenerator(name = "persona_seq", sequenceName = "SEQ_PERSONA", allocationSize = 1)
    @Column(name = "ID_PERSONA")
    private Long idPersona;

    @NotBlank
    @Column(name = "NOMBRE", nullable = false, length = 100)
    private String nombre;

    @NotBlank
    @Column(name = "APELLIDO", nullable = false, length = 100)
    private String apellido;

    @Column(name = "TIPO_DOC", length = 10)
    private String tipoDoc;

    @Column(name = "DOCUMENTO", length = 20)
    private String documento;

    @Email
    @Column(name = "EMAIL", length = 150)
    private String email;

    @Column(name = "TELEFONO", length = 20)
    private String telefono;

    public Persona() {
    }

    public Persona(String nombre, String apellido, String tipoDoc, String documento, String email, String telefono) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.tipoDoc = tipoDoc;
        this.documento = documento;
        this.email = email;
        this.telefono = telefono;
    }

    public Long getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(Long idPersona) {
        this.idPersona = idPersona;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getTipoDoc() {
        return tipoDoc;
    }

    public void setTipoDoc(String tipoDoc) {
        this.tipoDoc = tipoDoc;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}
