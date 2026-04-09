package com.project.hotel.controller;

import com.project.hotel.dto.TipoHabitacionRequest;
import com.project.hotel.entities.TipoHabitacion;
import com.project.hotel.service.TipoHabitacionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * REST API para gestión de tipos de habitación.
 *
 * GET    /api/tipos-habitacion            — Lista todos los tipos
 * GET    /api/tipos-habitacion/{id}       — Obtiene un tipo por ID
 * POST   /api/tipos-habitacion            — Crea un nuevo tipo
 * PUT    /api/tipos-habitacion/{id}       — Actualiza un tipo existente
 * DELETE /api/tipos-habitacion/{id}       — Elimina un tipo
 */
@RestController
@RequestMapping("/api/tipos-habitacion")
public class TipoHabitacionApiController {

    private final TipoHabitacionService tipoHabitacionService;

    public TipoHabitacionApiController(TipoHabitacionService tipoHabitacionService) {
        this.tipoHabitacionService = tipoHabitacionService;
    }

    @GetMapping
    public ResponseEntity<List<TipoHabitacion>> listar() {
        return ResponseEntity.ok(tipoHabitacionService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Long id) {
        return tipoHabitacionService.buscarPorId(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Tipo de habitación no encontrado: " + id)));
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody TipoHabitacionRequest request) {
        try {
            TipoHabitacion tipo = new TipoHabitacion();
            mapToEntity(tipo, request);
            TipoHabitacion creado = tipoHabitacionService.guardar(tipo);
            return ResponseEntity
                    .created(URI.create("/api/tipos-habitacion/" + creado.getIdTipo()))
                    .body(creado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno al crear tipo de habitación: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody TipoHabitacionRequest request) {
        return tipoHabitacionService.buscarPorId(id)
                .map(tipo -> {
                    mapToEntity(tipo, request);
                    return ResponseEntity.ok(tipoHabitacionService.guardar(tipo));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Tipo de habitación no encontrado: " + id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        if (tipoHabitacionService.buscarPorId(id).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Tipo de habitación no encontrado: " + id));
        }
        tipoHabitacionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    private void mapToEntity(TipoHabitacion tipo, TipoHabitacionRequest req) {
        tipo.setNombre(req.getNombre());
        tipo.setDescripcion(req.getDescripcion());
        tipo.setTarifaBase(req.getTarifaBase());
    }
}
