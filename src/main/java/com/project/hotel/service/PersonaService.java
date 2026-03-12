package com.project.hotel.service;

import com.project.hotel.entities.Persona;
import com.project.hotel.repository.PersonaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PersonaService {

    private final PersonaRepository personaRepository;

    public PersonaService(PersonaRepository personaRepository) {
        this.personaRepository = personaRepository;
    }

    public List<Persona> listarTodos() {
        return personaRepository.findAll();
    }

    public Optional<Persona> buscarPorId(Long id) {
        return personaRepository.findById(id);
    }

    public Persona guardar(Persona persona) {
        return personaRepository.save(persona);
    }

    public void eliminar(Long id) {
        personaRepository.deleteById(id);
    }

    public List<Persona> buscarPorNombreOApellido(String termino) {
        return personaRepository.findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(termino, termino);
    }
}
