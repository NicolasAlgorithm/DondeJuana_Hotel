package com.project.hotel.controller;

import com.project.hotel.dto.ReservaCreateRequest;
import com.project.hotel.dto.ReservaDetalleResponse;
import com.project.hotel.dto.ReservaUpdateRequest;
import com.project.hotel.entities.Reserva;
import com.project.hotel.service.ReservaService;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;

@RestController
@RequestMapping("/api/reservas")
public class ReservaApiController {

    private final ReservaService reservaService;

    public ReservaApiController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    // GET /api/reservas — lista todas las reservas
    @GetMapping
    public ResponseEntity<?> listar() {
        try {
            return ResponseEntity.ok(reservaService.listarTodos());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno al listar reservas: " + rootMessage(e)));
        }
    }

    // GET /api/reservas/{id} — obtiene una reserva por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Long id) {
        try {
            return reservaService.buscarPorId(id)
                    .<ResponseEntity<?>>map(r -> ResponseEntity.ok()
                            .cacheControl(CacheControl.noStore())
                            .body(toDetalleResponse(r)))
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(Map.of("error", "Reserva no encontrada: " + id)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno al obtener reserva: " + rootMessage(e)));
        }
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
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Error de integridad en BD: " + rootMessage(e)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno al crear reserva: " + rootMessage(e)));
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
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Error de integridad en BD: " + rootMessage(e)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno al modificar reserva: " + rootMessage(e)));
        }
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelar(@PathVariable Long id) {
        try {
            Reserva cancelada = reservaService.cancelar(id);
            return ResponseEntity.ok(cancelada);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno al cancelar reserva: " + rootMessage(e)));
        }
    }

    @PatchMapping("/{id}/checkin")
    public ResponseEntity<?> checkIn(@PathVariable Long id) {
        try {
            Reserva actualizada = reservaService.registrarCheckIn(id);
            return ResponseEntity.ok(actualizada);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno al registrar check-in: " + rootMessage(e)));
        }
    }

    @PatchMapping("/{id}/checkout")
    public ResponseEntity<?> checkOut(
            @PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaSalidaReal
    ) {
        try {
            Reserva actualizada = reservaService.registrarCheckOut(id, fechaSalidaReal);
            return ResponseEntity.ok(actualizada);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno al registrar check-out: " + rootMessage(e)));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> borrar(@PathVariable Long id) {
        try {
            reservaService.borrar(id);
            return ResponseEntity.noContent().build();

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno al borrar reserva: " + rootMessage(e)));
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
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno al validar disponibilidad: " + rootMessage(e)));
        }
    }

    private String rootMessage(Throwable t) {
        Throwable cause = t;
        while (cause.getCause() != null && cause.getCause() != cause) {
            cause = cause.getCause();
        }
        return cause.getMessage() != null ? cause.getMessage() : t.getMessage();
    }

    private ReservaDetalleResponse toDetalleResponse(Reserva r) {
        ReservaDetalleResponse dto = new ReservaDetalleResponse();
        dto.setIdReserva(r.getIdReserva());
        dto.setFechaEntrada(r.getFechaEntrada());
        dto.setFechaSalida(r.getFechaSalida());
        dto.setEstado(r.getEstado());

        if (r.getPersona() != null) {
            dto.setIdPersona(r.getPersona().getIdPersona());
            String nombre = (r.getPersona().getNombre() != null ? r.getPersona().getNombre() : "");
            String apellido = (r.getPersona().getApellido() != null ? r.getPersona().getApellido() : "");
            dto.setNombreHuesped((nombre + " " + apellido).trim());
        }

        if (r.getHabitacion() != null) {
            dto.setIdHabitacion(r.getHabitacion().getIdHabitacion());
            dto.setNumeroHabitacion(r.getHabitacion().getNumero());
        }

        return dto;
    }
}
