package com.project.hotel.controller;

import com.project.hotel.dto.PersonaRequest;
import com.project.hotel.entities.Persona;
import com.project.hotel.service.PersonaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * REST API para gestión de huéspedes (personas).
 *
 * GET    /api/personas            — Lista todos los huéspedes
 * GET    /api/personas/{id}       — Obtiene un huésped por ID
 * POST   /api/personas            — Crea un nuevo huésped
 * PUT    /api/personas/{id}       — Actualiza un huésped existente
 * DELETE /api/personas/{id}       — Elimina un huésped
 */
@RestController
@RequestMapping("/api/personas")
public class PersonaApiController {

    private final PersonaService personaService;

    public PersonaApiController(PersonaService personaService) {
        this.personaService = personaService;
    }

    @GetMapping
    public ResponseEntity<List<Persona>> listar() {
        return ResponseEntity.ok(personaService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Long id) {
        return personaService.buscarPorId(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Huésped no encontrado: " + id)));
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody PersonaRequest request) {
        try {
            Persona p = mapToEntity(new Persona(), request);
            Persona creada = personaService.guardar(p);
            return ResponseEntity
                    .created(URI.create("/api/personas/" + creada.getIdPersona()))
                    .body(creada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno al crear huésped"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody PersonaRequest request) {
        try {
            return personaService.buscarPorId(id)
                    .<ResponseEntity<?>>map(p -> {
                        mapToEntity(p, request);
                        return ResponseEntity.ok(personaService.guardar(p));
                    })
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(Map.of("error", "Huésped no encontrado: " + id)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno al actualizar huésped"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        if (personaService.buscarPorId(id).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Huésped no encontrado: " + id));
        }
        personaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    private Persona mapToEntity(Persona p, PersonaRequest req) {
        p.setNombre(req.getNombre());
        p.setApellido(req.getApellido());
        p.setTipoDoc(req.getTipoDoc());
        p.setDocumento(req.getDocumento());
        p.setEmail(req.getEmail());
        p.setTelefono(req.getTelefono());
        return p;
    }
}
