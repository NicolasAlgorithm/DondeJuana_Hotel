package com.project.hotel.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class PersonaRequest {

    @NotBlank
    @Size(min = 2, max = 150)
    @Pattern(regexp = "^[\\p{L} .'-]+$", message = "Nombre con formato invalido")
    private String nombre;

    @NotBlank
    @Size(min = 2, max = 150)
    @Pattern(regexp = "^[\\p{L} .'-]+$", message = "Apellido con formato invalido")
    private String apellido;

    @NotBlank
    @Size(min = 2, max = 20)
    @Pattern(regexp = "^[A-Za-z_]+$", message = "Tipo de documento invalido")
    private String tipoDoc;

    @NotBlank
    @Size(min = 4, max = 30)
    @Pattern(regexp = "^[A-Za-z0-9-]+$", message = "Documento con formato invalido")
    private String documento;

    @Email
    @Size(max = 150)
    private String email;

    @Size(max = 30)
    @Pattern(regexp = "^[0-9+()\\- ]*$", message = "Telefono con formato invalido")
    private String telefono;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getTipoDoc() { return tipoDoc; }
    public void setTipoDoc(String tipoDoc) { this.tipoDoc = tipoDoc; }

    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
}
