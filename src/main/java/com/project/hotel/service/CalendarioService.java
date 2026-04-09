package com.project.hotel.service;

import com.project.hotel.dto.CalendarioResponse;
import com.project.hotel.dto.DiaEstadoDTO;
import com.project.hotel.dto.EstadoCalendario;
import com.project.hotel.dto.HabitacionCalendarioDTO;
import com.project.hotel.entities.Habitacion;
import com.project.hotel.entities.Reserva;
import com.project.hotel.repository.HabitacionRepository;
import com.project.hotel.repository.ReservaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio que construye la vista de calendario de ocupación de habitaciones.
 *
 * <p>Para un rango de fechas dado devuelve, por cada habitación activa,
 * el estado de cada día dentro del rango. Los estados posibles son:
 * {@link EstadoCalendario#DISPONIBLE}, {@link EstadoCalendario#OCUPADA},
 * {@link EstadoCalendario#MANTENIMIENTO} y {@link EstadoCalendario#FUERA_DE_SERVICIO}.</p>
 *
 * <h3>Reglas de estado por día:</h3>
 * <ol>
 *   <li>Si {@code activo = 'N'} → {@code FUERA_DE_SERVICIO}.</li>
 *   <li>Si {@code estado = 'MANTENIMIENTO'} → {@code MANTENIMIENTO} en todos los días.</li>
 *   <li>Si existe una reserva no cancelada que cubre ese día → {@code OCUPADA}.</li>
 *   <li>En cualquier otro caso → {@code DISPONIBLE}.</li>
 * </ol>
 */
@Service
@Transactional(readOnly = true)
public class CalendarioService {

    /** Máximo número de días que puede abarcar un rango de consulta. */
    public static final int MAX_DIAS = 90;

    private final HabitacionRepository habitacionRepository;
    private final ReservaRepository reservaRepository;

    public CalendarioService(HabitacionRepository habitacionRepository,
                             ReservaRepository reservaRepository) {
        this.habitacionRepository = habitacionRepository;
        this.reservaRepository = reservaRepository;
    }

    /**
     * Genera el calendario de ocupación para el rango y filtros indicados.
     *
     * @param fechaInicio       primer día del rango (incluido, no nulo)
     * @param fechaFin          último día del rango (excluido, no nulo)
     * @param idTipoHabitacion  filtro opcional por tipo de habitación
     * @param piso              filtro opcional por piso
     * @return {@link CalendarioResponse} con la lista de habitaciones y sus
     *         estados por día
     * @throws IllegalArgumentException si el rango es inválido o supera
     *                                  {@link #MAX_DIAS} días
     */
    public CalendarioResponse obtenerCalendario(
            LocalDate fechaInicio,
            LocalDate fechaFin,
            Long idTipoHabitacion,
            Integer piso
    ) {
        validarRango(fechaInicio, fechaFin);

        List<Habitacion> habitaciones = obtenerHabitaciones(idTipoHabitacion, piso);

        if (habitaciones.isEmpty()) {
            return buildResponse(fechaInicio, fechaFin, Collections.emptyList());
        }

        List<Long> ids = habitaciones.stream()
                .map(Habitacion::getIdHabitacion)
                .collect(Collectors.toList());

        List<Reserva> reservas = reservaRepository
                .findActivasEnRangoParaHabitaciones(ids, fechaInicio, fechaFin);

        Map<Long, List<Reserva>> reservasPorHabitacion = reservas.stream()
                .collect(Collectors.groupingBy(r -> r.getHabitacion().getIdHabitacion()));

        List<HabitacionCalendarioDTO> dtos = habitaciones.stream()
                .map(h -> buildHabitacionDTO(
                        h, fechaInicio, fechaFin,
                        reservasPorHabitacion.getOrDefault(h.getIdHabitacion(), Collections.emptyList())
                ))
                .collect(Collectors.toList());

        return buildResponse(fechaInicio, fechaFin, dtos);
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private List<Habitacion> obtenerHabitaciones(Long idTipo, Integer piso) {
        if (idTipo != null && piso != null) {
            return habitacionRepository.findByTipoHabitacion_IdTipoAndPisoAndActivo(idTipo, piso, "S");
        }
        if (idTipo != null) {
            return habitacionRepository.findByTipoHabitacion_IdTipoAndActivo(idTipo, "S");
        }
        if (piso != null) {
            return habitacionRepository.findByPisoAndActivo(piso, "S");
        }
        return habitacionRepository.findByActivo("S");
    }

    private HabitacionCalendarioDTO buildHabitacionDTO(
            Habitacion h,
            LocalDate fechaInicio,
            LocalDate fechaFin,
            List<Reserva> reservas
    ) {
        HabitacionCalendarioDTO dto = new HabitacionCalendarioDTO();
        dto.setIdHabitacion(h.getIdHabitacion());
        dto.setCodigo(h.getCodigo());
        dto.setNumero(h.getNumero());
        dto.setPiso(h.getPiso());
        dto.setTarifaNoche(h.getTarifaNoche());
        if (h.getTipoHabitacion() != null) {
            dto.setIdTipoHabitacion(h.getTipoHabitacion().getIdTipo());
            dto.setNombreTipoHabitacion(h.getTipoHabitacion().getNombre());
        }

        boolean fueraDeServicio = !"S".equalsIgnoreCase(h.getActivo());
        boolean enMantenimiento = "MANTENIMIENTO".equalsIgnoreCase(h.getEstado());

        List<DiaEstadoDTO> dias = new ArrayList<>();
        LocalDate cursor = fechaInicio;
        while (cursor.isBefore(fechaFin)) {
            final LocalDate dia = cursor;
            EstadoCalendario estado;
            Long idReserva = null;

            if (fueraDeServicio) {
                estado = EstadoCalendario.FUERA_DE_SERVICIO;
            } else if (enMantenimiento) {
                estado = EstadoCalendario.MANTENIMIENTO;
            } else {
                Optional<Reserva> reservaDelDia = reservas.stream()
                        .filter(r -> !dia.isBefore(r.getFechaEntrada()) && dia.isBefore(r.getFechaSalida()))
                        .findFirst();
                if (reservaDelDia.isPresent()) {
                    estado = EstadoCalendario.OCUPADA;
                    idReserva = reservaDelDia.get().getIdReserva();
                } else {
                    estado = EstadoCalendario.DISPONIBLE;
                }
            }

            dias.add(new DiaEstadoDTO(dia, estado, idReserva));
            cursor = cursor.plusDays(1);
        }

        dto.setDias(dias);
        return dto;
    }

    private CalendarioResponse buildResponse(
            LocalDate fechaInicio,
            LocalDate fechaFin,
            List<HabitacionCalendarioDTO> habitaciones
    ) {
        CalendarioResponse resp = new CalendarioResponse();
        resp.setFechaInicio(fechaInicio);
        resp.setFechaFin(fechaFin);
        resp.setTotalDias((int) fechaInicio.datesUntil(fechaFin).count());
        resp.setTotalHabitaciones(habitaciones.size());
        resp.setHabitaciones(habitaciones);
        return resp;
    }

    private void validarRango(LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            throw new IllegalArgumentException("fechaInicio y fechaFin son obligatorios");
        }
        if (!fechaInicio.isBefore(fechaFin)) {
            throw new IllegalArgumentException("fechaInicio debe ser anterior a fechaFin");
        }
        long dias = fechaInicio.datesUntil(fechaFin).count();
        if (dias > MAX_DIAS) {
            throw new IllegalArgumentException(
                    "El rango máximo permitido es " + MAX_DIAS + " días (solicitado: " + dias + ")");
        }
    }
}
