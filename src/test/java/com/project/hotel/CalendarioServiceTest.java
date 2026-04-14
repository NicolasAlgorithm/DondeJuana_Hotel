package com.project.hotel;

import com.project.hotel.dto.CalendarioResponse;
import com.project.hotel.dto.DiaEstadoDTO;
import com.project.hotel.dto.EstadoCalendario;
import com.project.hotel.dto.HabitacionCalendarioDTO;
import com.project.hotel.entities.Habitacion;
import com.project.hotel.entities.Persona;
import com.project.hotel.entities.Reserva;
import com.project.hotel.entities.TipoHabitacion;
import com.project.hotel.repository.HabitacionRepository;
import com.project.hotel.repository.ReservaRepository;
import com.project.hotel.service.CalendarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CalendarioServiceTest {

    @Mock
    private HabitacionRepository habitacionRepository;
    @Mock
    private ReservaRepository reservaRepository;

    @InjectMocks
    private CalendarioService calendarioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ── validación de rango ──────────────────────────────────────────────────

    @Test
    void obtenerCalendario_lanzaExcepcion_cuandoFechaFinNoEsDespuesDeInicio() {
        assertThrows(IllegalArgumentException.class,
                () -> calendarioService.obtenerCalendario(
                        LocalDate.of(2026, 4, 10),
                        LocalDate.of(2026, 4, 5),
                        null, null));
    }

    @Test
    void obtenerCalendario_lanzaExcepcion_cuandoFechasIguales() {
        LocalDate hoy = LocalDate.of(2026, 4, 1);
        assertThrows(IllegalArgumentException.class,
                () -> calendarioService.obtenerCalendario(hoy, hoy, null, null));
    }

    @Test
    void obtenerCalendario_lanzaExcepcion_cuandoRangoSuperaMaximo() {
        LocalDate inicio = LocalDate.of(2026, 1, 1);
        LocalDate fin = inicio.plusDays(CalendarioService.MAX_DIAS + 1);
        assertThrows(IllegalArgumentException.class,
                () -> calendarioService.obtenerCalendario(inicio, fin, null, null));
    }

    // ── sin habitaciones ────────────────────────────────────────────────────

    @Test
    void obtenerCalendario_retornaListaVacia_cuandoNoHayHabitaciones() {
        when(habitacionRepository.findByActivo("S")).thenReturn(Collections.emptyList());

        CalendarioResponse resp = calendarioService.obtenerCalendario(
                LocalDate.of(2026, 4, 1),
                LocalDate.of(2026, 4, 8),
                null, null);

        assertNotNull(resp);
        assertEquals(0, resp.getTotalHabitaciones());
        assertTrue(resp.getHabitaciones().isEmpty());
        assertEquals(7, resp.getTotalDias());
    }

    // ── habitación disponible ────────────────────────────────────────────────

    @Test
    void obtenerCalendario_marcaDiasComoDisponible_cuandoNoHayReservas() {
        Habitacion h = habitacionActiva(1L, "HAB-101", "101", "DISPONIBLE");
        when(habitacionRepository.findByActivo("S")).thenReturn(List.of(h));
        when(reservaRepository.findActivasEnRangoParaHabitaciones(anyList(), any(), any()))
                .thenReturn(Collections.emptyList());

        LocalDate inicio = LocalDate.of(2026, 4, 1);
        LocalDate fin = LocalDate.of(2026, 4, 4);

        CalendarioResponse resp = calendarioService.obtenerCalendario(inicio, fin, null, null);

        HabitacionCalendarioDTO dto = resp.getHabitaciones().get(0);
        assertEquals(3, dto.getDias().size());
        dto.getDias().forEach(d -> {
            assertEquals(EstadoCalendario.DISPONIBLE, d.getEstado());
            assertEquals("#28a745", d.getCodigoColor());
            assertNull(d.getIdReserva());
        });
    }

    // ── habitación ocupada ───────────────────────────────────────────────────

    @Test
    void obtenerCalendario_marcaDiasComoOcupada_cuandoExisteReservaActiva() {
        Habitacion h = habitacionActiva(2L, "HAB-102", "102", "DISPONIBLE");
        when(habitacionRepository.findByActivo("S")).thenReturn(List.of(h));

        Reserva r = reserva(10L, h, LocalDate.of(2026, 4, 2), LocalDate.of(2026, 4, 5), "ACTIVA");
        when(reservaRepository.findActivasEnRangoParaHabitaciones(anyList(), any(), any()))
                .thenReturn(List.of(r));

        LocalDate inicio = LocalDate.of(2026, 4, 1);
        LocalDate fin = LocalDate.of(2026, 4, 6);

        CalendarioResponse resp = calendarioService.obtenerCalendario(inicio, fin, null, null);

        List<DiaEstadoDTO> dias = resp.getHabitaciones().get(0).getDias();
        assertEquals(EstadoCalendario.DISPONIBLE, dias.get(0).getEstado()); // 2026-04-01
        assertEquals(EstadoCalendario.OCUPADA,    dias.get(1).getEstado()); // 2026-04-02
        assertEquals(EstadoCalendario.OCUPADA,    dias.get(2).getEstado()); // 2026-04-03
        assertEquals(EstadoCalendario.OCUPADA,    dias.get(3).getEstado()); // 2026-04-04
        assertEquals(EstadoCalendario.DISPONIBLE, dias.get(4).getEstado()); // 2026-04-05 (fin excluido)

        // El id de reserva debe estar presente en días ocupados
        assertEquals(10L, dias.get(1).getIdReserva());
    }

    // ── habitación en mantenimiento ──────────────────────────────────────────

    @Test
    void obtenerCalendario_marcaTodosLosDiasComoMantenimiento_cuandoEstadoEsMantenimiento() {
        Habitacion h = habitacionActiva(3L, "HAB-103", "103", "MANTENIMIENTO");
        when(habitacionRepository.findByActivo("S")).thenReturn(List.of(h));
        when(reservaRepository.findActivasEnRangoParaHabitaciones(anyList(), any(), any()))
                .thenReturn(Collections.emptyList());

        CalendarioResponse resp = calendarioService.obtenerCalendario(
                LocalDate.of(2026, 4, 1), LocalDate.of(2026, 4, 4), null, null);

        resp.getHabitaciones().get(0).getDias().forEach(d -> {
            assertEquals(EstadoCalendario.MANTENIMIENTO, d.getEstado());
            assertEquals("#ffc107", d.getCodigoColor());
        });
    }

    // ── habitación fuera de servicio ─────────────────────────────────────────

    @Test
    void obtenerCalendario_marcaTodosLosDiasComoFueraDeServicio_cuandoActivoEsN() {
        Habitacion h = habitacionInactiva(4L, "HAB-104", "104");
        when(habitacionRepository.findByActivo("S")).thenReturn(List.of(h));
        when(reservaRepository.findActivasEnRangoParaHabitaciones(anyList(), any(), any()))
                .thenReturn(Collections.emptyList());

        CalendarioResponse resp = calendarioService.obtenerCalendario(
                LocalDate.of(2026, 4, 1), LocalDate.of(2026, 4, 4), null, null);

        resp.getHabitaciones().get(0).getDias().forEach(d ->
                assertEquals(EstadoCalendario.FUERA_DE_SERVICIO, d.getEstado()));
    }

    // ── filtro por tipo de habitación ────────────────────────────────────────

    @Test
    void obtenerCalendario_usaFiltroTipo_cuandoIdTipoProporcionado() {
        when(habitacionRepository.findByTipoHabitacion_IdTipoAndActivo(5L, "S"))
                .thenReturn(Collections.emptyList());

        calendarioService.obtenerCalendario(
                LocalDate.of(2026, 4, 1), LocalDate.of(2026, 4, 4), 5L, null);

        verify(habitacionRepository, times(1))
                .findByTipoHabitacion_IdTipoAndActivo(5L, "S");
        verify(habitacionRepository, never()).findByActivo(any());
    }

    // ── filtro por piso ──────────────────────────────────────────────────────

    @Test
    void obtenerCalendario_usaFiltroPiso_cuandoPisoProporcionado() {
        when(habitacionRepository.findByPisoAndActivo(2, "S"))
                .thenReturn(Collections.emptyList());

        calendarioService.obtenerCalendario(
                LocalDate.of(2026, 4, 1), LocalDate.of(2026, 4, 4), null, 2);

        verify(habitacionRepository, times(1)).findByPisoAndActivo(2, "S");
        verify(habitacionRepository, never()).findByActivo(any());
    }

    // ── filtro combinado tipo + piso ─────────────────────────────────────────

    @Test
    void obtenerCalendario_usaFiltroCombinado_cuandoTipoYPisoProporcionados() {
        when(habitacionRepository.findByTipoHabitacion_IdTipoAndPisoAndActivo(5L, 2, "S"))
                .thenReturn(Collections.emptyList());

        calendarioService.obtenerCalendario(
                LocalDate.of(2026, 4, 1), LocalDate.of(2026, 4, 4), 5L, 2);

        verify(habitacionRepository, times(1))
                .findByTipoHabitacion_IdTipoAndPisoAndActivo(5L, 2, "S");
    }

    // ── estructura de respuesta ──────────────────────────────────────────────

    @Test
    void obtenerCalendario_retornaMetadatosCorrectos() {
        when(habitacionRepository.findByActivo("S")).thenReturn(Collections.emptyList());

        LocalDate inicio = LocalDate.of(2026, 4, 1);
        LocalDate fin = LocalDate.of(2026, 4, 15);

        CalendarioResponse resp = calendarioService.obtenerCalendario(inicio, fin, null, null);

        assertEquals(inicio, resp.getFechaInicio());
        assertEquals(fin, resp.getFechaFin());
        assertEquals(14, resp.getTotalDias());
        assertEquals(0, resp.getTotalHabitaciones());
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private Habitacion habitacionActiva(Long id, String codigo, String numero, String estado) {
        Habitacion h = new Habitacion();
        h.setIdHabitacion(id);
        h.setCodigo(codigo);
        h.setNumero(numero);
        h.setEstado(estado);
        h.setActivo("S");
        h.setTarifaNoche(BigDecimal.valueOf(100));
        TipoHabitacion tipo = new TipoHabitacion("Estándar", "", BigDecimal.valueOf(100));
        tipo.setIdTipo(1L);
        h.setTipoHabitacion(tipo);
        return h;
    }

    private Habitacion habitacionInactiva(Long id, String codigo, String numero) {
        Habitacion h = habitacionActiva(id, codigo, numero, "DISPONIBLE");
        h.setActivo("N");
        return h;
    }

    private Reserva reserva(Long id, Habitacion h, LocalDate entrada, LocalDate salida, String estado) {
        Reserva r = new Reserva();
        r.setIdReserva(id);
        r.setHabitacion(h);
        r.setPersona(new Persona());
        r.setFechaEntrada(entrada);
        r.setFechaSalida(salida);
        r.setEstado(estado);
        return r;
    }
}
