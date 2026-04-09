package com.project.hotel.controller;

import com.project.hotel.dto.CalendarioResponse;
import com.project.hotel.service.CalendarioService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Map;

/**
 * Controlador REST para la vista de calendario de reservas de habitaciones.
 *
 * <p>Base URL: {@code /api/calendario}</p>
 *
 * <h2>Endpoints</h2>
 *
 * <h3>GET /api/calendario</h3>
 * <p>Devuelve el estado de cada habitación activa para cada día dentro del
 * rango de fechas indicado. Los estados están codificados con colores listos
 * para consumir desde un frontend tipo agenda/calendario.</p>
 *
 * <h4>Parámetros de consulta</h4>
 * <ul>
 *   <li>{@code fechaInicio} (obligatorio) – Primer día del rango (yyyy-MM-dd, incluido).</li>
 *   <li>{@code fechaFin}    (obligatorio) – Último día del rango (yyyy-MM-dd, excluido).</li>
 *   <li>{@code idTipoHabitacion} (opcional) – Filtra solo habitaciones del tipo indicado.</li>
 *   <li>{@code piso}        (opcional) – Filtra solo habitaciones del piso indicado.</li>
 * </ul>
 *
 * <h4>Restricciones</h4>
 * <ul>
 *   <li>El rango máximo es de 90 días.</li>
 *   <li>{@code fechaInicio} debe ser anterior a {@code fechaFin}.</li>
 * </ul>
 *
 * <h4>Ejemplo de llamada</h4>
 * <pre>{@code
 * GET /api/calendario?fechaInicio=2026-04-01&fechaFin=2026-04-08
 * GET /api/calendario?fechaInicio=2026-04-01&fechaFin=2026-04-30&idTipoHabitacion=2
 * GET /api/calendario?fechaInicio=2026-04-01&fechaFin=2026-04-08&piso=3
 * }</pre>
 *
 * <h4>Respuesta 200 OK (fragmento)</h4>
 * <pre>{@code
 * {
 *   "fechaInicio": "2026-04-01",
 *   "fechaFin": "2026-04-08",
 *   "totalDias": 7,
 *   "totalHabitaciones": 10,
 *   "habitaciones": [
 *     {
 *       "idHabitacion": 5,
 *       "codigo": "HAB-105",
 *       "numero": "105",
 *       "piso": 1,
 *       "idTipoHabitacion": 2,
 *       "nombreTipoHabitacion": "Suite",
 *       "tarifaNoche": 150.00,
 *       "dias": [
 *         { "fecha": "2026-04-01", "estado": "DISPONIBLE",    "etiquetaEstado": "Disponible",        "codigoColor": "#28a745", "idReserva": null },
 *         { "fecha": "2026-04-02", "estado": "OCUPADA",       "etiquetaEstado": "Ocupada",            "codigoColor": "#dc3545", "idReserva": 42   },
 *         { "fecha": "2026-04-03", "estado": "MANTENIMIENTO", "etiquetaEstado": "En mantenimiento",  "codigoColor": "#ffc107", "idReserva": null }
 *       ]
 *     }
 *   ]
 * }
 * }</pre>
 *
 * <h4>Respuesta 400 Bad Request</h4>
 * <pre>{@code { "error": "fechaInicio debe ser anterior a fechaFin" }}</pre>
 *
 * <h4>Seguridad</h4>
 * <p>Requiere autenticación con roles {@code ADMINISTRADOR} o {@code RECEPCIONISTA}.</p>
 */
@RestController
@RequestMapping("/api/calendario")
public class CalendarioController {

    private final CalendarioService calendarioService;

    public CalendarioController(CalendarioService calendarioService) {
        this.calendarioService = calendarioService;
    }

    /**
     * Obtiene el calendario de ocupación de habitaciones para un rango de fechas.
     *
     * @param fechaInicio       primer día del rango (incluido)
     * @param fechaFin          último día del rango (excluido)
     * @param idTipoHabitacion  filtro opcional por tipo de habitación
     * @param piso              filtro opcional por piso
     * @return {@link CalendarioResponse} con la lista de habitaciones y su
     *         estado por día, o un cuerpo {@code {"error": "..."}} en caso de
     *         parámetros inválidos
     */
    @GetMapping
    public ResponseEntity<?> obtenerCalendario(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) Long idTipoHabitacion,
            @RequestParam(required = false) Integer piso
    ) {
        try {
            CalendarioResponse response = calendarioService.obtenerCalendario(
                    fechaInicio, fechaFin, idTipoHabitacion, piso
            );
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno al obtener el calendario: " + rootMessage(e)));
        }
    }

    private String rootMessage(Throwable t) {
        Throwable cause = t;
        while (cause.getCause() != null && cause.getCause() != cause) {
            cause = cause.getCause();
        }
        return cause.getMessage() != null ? cause.getMessage() : t.getMessage();
    }
}
