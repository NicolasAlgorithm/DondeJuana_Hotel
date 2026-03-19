package com.project.hotel.controller;

import com.project.hotel.dto.EstadoHabitacionRequest;
import com.project.hotel.dto.HabitacionRequest;
import com.project.hotel.model.Habitacion;
import com.project.hotel.service.HabitacionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/habitaciones")
public class HabitacionController {

    private final HabitacionService habitacionService;

    public HabitacionController(HabitacionService habitacionService) {
        this.habitacionService = habitacionService;
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody HabitacionRequest request) {
        try {
            Habitacion creada = habitacionService.crear(request);
            return ResponseEntity
                    .created(URI.create("/api/habitaciones/" + creada.getIdHabitacion()))
                    .body(creada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<Habitacion>> listar() {
        return ResponseEntity.ok(habitacionService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(habitacionService.obtenerPorId(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody HabitacionRequest request) {
        try {
            return ResponseEntity.ok(habitacionService.actualizar(id, request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> actualizarEstado(@PathVariable Long id, @Valid @RequestBody EstadoHabitacionRequest request) {
        try {
            return ResponseEntity.ok(habitacionService.actualizarEstado(id, request.getEstado()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}