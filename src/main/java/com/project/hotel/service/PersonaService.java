package com.project.hotel.service;

import com.project.hotel.entities.Persona;
import com.project.hotel.repository.PersonaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PersonaService {

    private final PersonaRepository personaRepository;
    private final InputSanitizer inputSanitizer;

    public PersonaService(PersonaRepository personaRepository, InputSanitizer inputSanitizer) {
        this.personaRepository = personaRepository;
        this.inputSanitizer = inputSanitizer;
    }

    public List<Persona> listarTodos() {
        return personaRepository.findAll();
    }

    public Optional<Persona> buscarPorId(Long id) {
        return personaRepository.findById(id);
    }

    public Persona guardar(Persona persona) {
        sanitizePersona(persona);
        return personaRepository.save(persona);
    }

    public void eliminar(Long id) {
        personaRepository.deleteById(id);
    }

    public List<Persona> buscarPorNombreOApellido(String termino) {
        String sanitizedTerm = inputSanitizer.sanitizePlainText(termino);
        if (sanitizedTerm == null || sanitizedTerm.isBlank()) {
            return List.of();
        }
        return personaRepository.findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(sanitizedTerm, sanitizedTerm);
    }

    private void sanitizePersona(Persona persona) {
        persona.setNombre(requireLength("nombre", inputSanitizer.sanitizePlainText(persona.getNombre()), 2, 150));
        persona.setApellido(requireLength("apellido", inputSanitizer.sanitizePlainText(persona.getApellido()), 2, 150));
        persona.setTipoDoc(requireLength("tipoDoc", inputSanitizer.sanitizeUpperCode(persona.getTipoDoc()), 2, 20));
        persona.setDocumento(requireLength("documento", inputSanitizer.sanitizeUpperCode(persona.getDocumento()), 4, 30));

        String email = inputSanitizer.sanitizePlainText(persona.getEmail());
        persona.setEmail((email == null || email.isBlank()) ? null : email);

        String telefono = inputSanitizer.sanitizePlainText(persona.getTelefono());
        persona.setTelefono((telefono == null || telefono.isBlank()) ? null : telefono);
    }

    private String requireLength(String field, String value, int min, int max) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Campo obligatorio: " + field);
        }
        if (value.length() < min || value.length() > max) {
            throw new IllegalArgumentException("Longitud invalida para " + field);
        }
        return value;
    }
}
