package com.project.hotel.controller;

import com.project.hotel.dto.ReservaCreateRequest;
import com.project.hotel.dto.ReservaUpdateRequest;
import com.project.hotel.entities.Reserva;
import com.project.hotel.service.ReservaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/reservas")
public class ReservaApiController {

    private final ReservaService reservaService;

    public ReservaApiController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody ReservaCreateRequest request) {
        try {
            Reserva creada = reservaService.crear(
                    request.getIdPersona(),
                    request.getIdHabitacion(),
                    request.getFechaEntrada(),
                    request.getFechaSalida(),
                    request.getEstado()
            );

            return ResponseEntity
                    .created(URI.create("/api/reservas/" + creada.getIdReserva()))
                    .body(creada);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> modificar(@PathVariable Long id, @Valid @RequestBody ReservaUpdateRequest request) {
        try {
            Reserva actualizada = reservaService.modificar(
                    id,
                    request.getIdPersona(),
                    request.getIdHabitacion(),
                    request.getFechaEntrada(),
                    request.getFechaSalida(),
                    request.getEstado()
            );
            return ResponseEntity.ok(actualizada);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelar(@PathVariable Long id) {
        try {
            Reserva cancelada = reservaService.cancelar(id);
            return ResponseEntity.ok(cancelada);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/disponibilidad")
    public ResponseEntity<?> disponibilidad(
            @RequestParam Long idHabitacion,
            @RequestParam LocalDate fechaEntrada,
            @RequestParam LocalDate fechaSalida,
            @RequestParam(required = false) Long idReservaExcluir
    ) {
        try {
            boolean disponible = reservaService.estaDisponible(
                    idHabitacion,
                    fechaEntrada,
                    fechaSalida,
                    idReservaExcluir
            );

            return ResponseEntity.ok(Map.of(
                    "idHabitacion", idHabitacion,
                    "fechaEntrada", fechaEntrada,
                    "fechaSalida", fechaSalida,
                    "disponible", disponible
            ));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
