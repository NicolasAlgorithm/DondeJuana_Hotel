package com.project.hotel;

import com.project.hotel.dto.PanelReporteDTO;
import com.project.hotel.entities.Habitacion;
import com.project.hotel.entities.Reserva;
import com.project.hotel.repository.HabitacionRepository;
import com.project.hotel.repository.ReservaRepository;
import com.project.hotel.service.PanelAdministrativoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

class PanelAdministrativoServiceTest {

    @Mock
    private HabitacionRepository habitacionRepository;
    @Mock
    private ReservaRepository reservaRepository;

    @InjectMocks
    private PanelAdministrativoService panelAdministrativoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void reporteDiario_calculaOcupacionEIngresos_correctamente() {
        Habitacion habitacion = habitacion(1L, BigDecimal.valueOf(100));
        Reserva reserva = reserva(10L, habitacion, LocalDate.of(2026, 4, 10), LocalDate.of(2026, 4, 12), "ACTIVA");

        when(habitacionRepository.findByActivo("S")).thenReturn(List.of(habitacion));
        when(reservaRepository.findActivasEnRangoParaHabitaciones(anyList(), any(), any()))
                .thenReturn(List.of(reserva));

        PanelReporteDTO dto = panelAdministrativoService.reporteDiario(LocalDate.of(2026, 4, 10));

        assertEquals(new BigDecimal("100.00"), dto.getOcupacionPorcentaje());
        assertEquals(new BigDecimal("100.00"), dto.getIngresos());
        assertEquals(1, dto.getReservasEnPeriodo());
        assertEquals(1, dto.getNochesOcupadas());
    }

    @Test
    void reporteSemanal_ignoraCanceladas_y_retornaSinDatosCuandoNoAplica() {
        Habitacion habitacion = habitacion(1L, BigDecimal.valueOf(120));
        Reserva cancelada = reserva(10L, habitacion, LocalDate.of(2026, 4, 7), LocalDate.of(2026, 4, 8), "CANCELADA");

        when(habitacionRepository.findByActivo("S")).thenReturn(List.of(habitacion));
        when(reservaRepository.findActivasEnRangoParaHabitaciones(anyList(), any(), any()))
                .thenReturn(List.of(cancelada));

        PanelReporteDTO dto = panelAdministrativoService.reporteSemanal(LocalDate.of(2026, 4, 8));

        assertEquals(0, dto.getReservasEnPeriodo());
        assertEquals(new BigDecimal("0.00"), dto.getIngresos());
        assertTrue(dto.isSinDatos());
    }

    private Habitacion habitacion(Long id, BigDecimal tarifa) {
        Habitacion h = new Habitacion();
        h.setIdHabitacion(id);
        h.setActivo("S");
        h.setTarifaNoche(tarifa);
        return h;
    }

    private Reserva reserva(Long id, Habitacion habitacion, LocalDate entrada, LocalDate salida, String estado) {
        Reserva r = new Reserva();
        r.setIdReserva(id);
        r.setHabitacion(habitacion);
        r.setFechaEntrada(entrada);
        r.setFechaSalida(salida);
        r.setEstado(estado);
        return r;
    }
}
