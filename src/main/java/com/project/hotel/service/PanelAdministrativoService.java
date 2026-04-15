package com.project.hotel.service;

import com.project.hotel.dto.PanelReporteDTO;
import com.project.hotel.entities.Habitacion;
import com.project.hotel.entities.Reserva;
import com.project.hotel.repository.HabitacionRepository;
import com.project.hotel.repository.ReservaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class PanelAdministrativoService {

    private final HabitacionRepository habitacionRepository;
    private final ReservaRepository reservaRepository;

    public PanelAdministrativoService(HabitacionRepository habitacionRepository, ReservaRepository reservaRepository) {
        this.habitacionRepository = habitacionRepository;
        this.reservaRepository = reservaRepository;
    }

    public PanelReporteDTO reporteDiario(LocalDate fecha) {
        LocalDate inicio = fecha != null ? fecha : LocalDate.now();
        return calcular("Diario", inicio, inicio.plusDays(1));
    }

    public PanelReporteDTO reporteSemanal(LocalDate fechaReferencia) {
        LocalDate base = fechaReferencia != null ? fechaReferencia : LocalDate.now();
        LocalDate inicio = base.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        return calcular("Semanal", inicio, inicio.plusDays(7));
    }

    public PanelReporteDTO reporteMensual(LocalDate fechaReferencia) {
        LocalDate base = fechaReferencia != null ? fechaReferencia : LocalDate.now();
        LocalDate inicio = base.withDayOfMonth(1);
        return calcular("Mensual", inicio, inicio.plusMonths(1));
    }

    private PanelReporteDTO calcular(String periodo, LocalDate fechaInicio, LocalDate fechaFinExclusiva) {
        List<Habitacion> habitacionesActivas = habitacionRepository.findByActivo("S");
        if (habitacionesActivas.isEmpty()) {
            return new PanelReporteDTO(
                    periodo,
                    fechaInicio,
                    fechaFinExclusiva.minusDays(1),
                    0,
                    0,
                    0,
                    BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP),
                    BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP),
                    0
            );
        }

        List<Long> idsHabitaciones = habitacionesActivas.stream()
                .map(Habitacion::getIdHabitacion)
                .toList();
        Set<Long> idsHabitacionesActivas = habitacionesActivas.stream()
                .map(Habitacion::getIdHabitacion)
                .collect(Collectors.toSet());

        long nochesDisponibles = (long) habitacionesActivas.size() * fechaInicio.datesUntil(fechaFinExclusiva).count();
        long nochesOcupadas = 0;
        BigDecimal ingresos = BigDecimal.ZERO;
        int reservasEnPeriodo = 0;

        List<Reserva> reservas = reservaRepository.findActivasEnRangoParaHabitaciones(
                idsHabitaciones, fechaInicio, fechaFinExclusiva);

        for (Reserva reserva : reservas) {
            if (reserva.getHabitacion() == null || reserva.getHabitacion().getIdHabitacion() == null) {
                continue;
            }
            if (!idsHabitacionesActivas.contains(reserva.getHabitacion().getIdHabitacion())) {
                continue;
            }
            if (reserva.getFechaEntrada() == null || reserva.getFechaSalida() == null) {
                continue;
            }
            if ("CANCELADA".equalsIgnoreCase(reserva.getEstado())) {
                continue;
            }

            LocalDate inicioOcupado = reserva.getFechaEntrada().isAfter(fechaInicio) ? reserva.getFechaEntrada() : fechaInicio;
            LocalDate finOcupado = reserva.getFechaSalida().isBefore(fechaFinExclusiva) ? reserva.getFechaSalida() : fechaFinExclusiva;
            if (!inicioOcupado.isBefore(finOcupado)) {
                continue;
            }

            long nochesReserva = inicioOcupado.datesUntil(finOcupado).count();
            nochesOcupadas += nochesReserva;
            reservasEnPeriodo++;

            BigDecimal tarifa = reserva.getHabitacion().getTarifaNoche() != null
                    ? reserva.getHabitacion().getTarifaNoche()
                    : BigDecimal.ZERO;
            ingresos = ingresos.add(tarifa.multiply(BigDecimal.valueOf(nochesReserva)));
        }

        BigDecimal ocupacionPorcentaje = nochesDisponibles == 0
                ? BigDecimal.ZERO
                : BigDecimal.valueOf(nochesOcupadas)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(nochesDisponibles), 2, RoundingMode.HALF_UP);

        return new PanelReporteDTO(
                periodo,
                fechaInicio,
                fechaFinExclusiva.minusDays(1),
                habitacionesActivas.size(),
                nochesDisponibles,
                nochesOcupadas,
                ocupacionPorcentaje,
                ingresos.setScale(2, RoundingMode.HALF_UP),
                reservasEnPeriodo
        );
    }
}
