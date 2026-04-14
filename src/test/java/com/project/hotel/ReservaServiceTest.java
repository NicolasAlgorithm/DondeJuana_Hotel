package com.project.hotel;

import com.project.hotel.entities.Habitacion;
import com.project.hotel.entities.Persona;
import com.project.hotel.entities.Reserva;
import com.project.hotel.repository.HabitacionRepository;
import com.project.hotel.repository.PersonaRepository;
import com.project.hotel.repository.ReservaRepository;
import com.project.hotel.service.ReservaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepository;
    @Mock
    private PersonaRepository personaRepository;
    @Mock
    private HabitacionRepository habitacionRepository;

    @InjectMocks
    private ReservaService reservaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ── crear ──────────────────────────────────────────────────────────────

    @Test
    void crear_lanzaExcepcion_cuandoFechaSalidaNoEsDespuesDeEntrada() {
        LocalDate entrada = LocalDate.of(2025, 6, 10);
        LocalDate salida  = LocalDate.of(2025, 6, 5);

        assertThrows(IllegalArgumentException.class,
                () -> reservaService.crear(1L, 1L, entrada, salida, "ACTIVA"));
    }

    @Test
    void crear_lanzaExcepcion_cuandoFechasIguales() {
        LocalDate fecha = LocalDate.of(2025, 6, 10);

        assertThrows(IllegalArgumentException.class,
                () -> reservaService.crear(1L, 1L, fecha, fecha, "ACTIVA"));
    }

    @Test
    void crear_lanzaExcepcion_cuandoHabitacionNoDisponible() {
        LocalDate entrada = LocalDate.of(2025, 6, 1);
        LocalDate salida  = LocalDate.of(2025, 6, 5);

        when(reservaRepository.existeTraslapeActivo(anyLong(), any(), any(), any()))
                .thenReturn(true);  // traslape encontrado → no disponible

        assertThrows(IllegalArgumentException.class,
                () -> reservaService.crear(1L, 1L, entrada, salida, "ACTIVA"));
    }

    @Test
    void crear_normalizaEstadoConfirmadaAActiva() {
        LocalDate entrada = LocalDate.of(2025, 6, 1);
        LocalDate salida  = LocalDate.of(2025, 6, 5);

        when(reservaRepository.existeTraslapeActivo(anyLong(), any(), any(), any())).thenReturn(false);
        when(personaRepository.findById(1L)).thenReturn(Optional.of(new Persona()));
        when(habitacionRepository.findById(1L)).thenReturn(Optional.of(new Habitacion()));
        when(reservaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Reserva result = reservaService.crear(1L, 1L, entrada, salida, "CONFIRMADA");

        assertEquals("ACTIVA", result.getEstado());
    }

    @Test
    void crear_lanzaExcepcion_cuandoEstadoEsInvalido() {
        LocalDate entrada = LocalDate.of(2025, 6, 1);
        LocalDate salida  = LocalDate.of(2025, 6, 5);

        when(reservaRepository.existeTraslapeActivo(anyLong(), any(), any(), any())).thenReturn(false);
        when(personaRepository.findById(1L)).thenReturn(Optional.of(new Persona()));
        when(habitacionRepository.findById(1L)).thenReturn(Optional.of(new Habitacion()));

        assertThrows(IllegalArgumentException.class,
                () -> reservaService.crear(1L, 1L, entrada, salida, "DESCONOCIDO"));
    }

    // ── cancelar ───────────────────────────────────────────────────────────

    @Test
    void cancelar_lanzaExcepcion_cuandoReservaEsCumplida() {
        Reserva r = new Reserva();
        r.setEstado("CUMPLIDA");
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(r));

        assertThrows(IllegalArgumentException.class, () -> reservaService.cancelar(1L));
    }

    @Test
    void cancelar_cambiaEstadoACancelada_cuandoReservaEstaActiva() {
        Reserva r = new Reserva();
        r.setEstado("ACTIVA");
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(r));
        when(reservaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Reserva result = reservaService.cancelar(1L);

        assertEquals("CANCELADA", result.getEstado());
    }

    @Test
    void cancelar_lanzaExcepcion_cuandoReservaNoExiste() {
        when(reservaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> reservaService.cancelar(99L));
    }

    // ── check-in / check-out ────────────────────────────────────────────────

    @Test
    void checkIn_lanzaExcepcion_cuandoReservaEsCancelada() {
        Reserva r = new Reserva();
        r.setEstado("CANCELADA");
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(r));

        assertThrows(IllegalArgumentException.class, () -> reservaService.registrarCheckIn(1L));
    }

    @Test
    void checkIn_lanzaExcepcion_cuandoReservaEsCumplida() {
        Reserva r = new Reserva();
        r.setEstado("CUMPLIDA");
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(r));

        assertThrows(IllegalArgumentException.class, () -> reservaService.registrarCheckIn(1L));
    }

    @Test
    void checkIn_cambiaEstadoAEnEstadia_yRegistraFechaHoraSimulada_cuandoReservaEsValida() {
        Reserva r = new Reserva();
        r.setEstado(null);
        r.setFechaEntrada(LocalDate.of(2025, 6, 1));
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(r));
        when(reservaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Reserva result = reservaService.registrarCheckIn(1L);

        assertEquals("EN_ESTADIA", result.getEstado());
        assertEquals(LocalDate.of(2025, 6, 1).atTime(15, 0), result.getFechaHoraCheckIn());
    }

    @Test
    void checkIn_cambiaEstadoAEnEstadia_cuandoReservaTieneEstadoVacio() {
        Reserva r = new Reserva();
        r.setEstado("");
        r.setFechaEntrada(LocalDate.of(2025, 6, 2));
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(r));
        when(reservaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Reserva result = reservaService.registrarCheckIn(1L);

        assertEquals("EN_ESTADIA", result.getEstado());
        assertEquals(LocalDate.of(2025, 6, 2).atTime(15, 0), result.getFechaHoraCheckIn());
    }

    @Test
    void checkOut_cambiaEstadoACumplida_cuandoReservaEstaEnEstadia() {
        Reserva r = new Reserva();
        r.setEstado("EN_ESTADIA");
        r.setFechaEntrada(LocalDate.of(2025, 6, 1));
        r.setFechaSalida(LocalDate.of(2025, 6, 5));
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(r));
        when(reservaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Reserva result = reservaService.registrarCheckOut(1L, LocalDate.of(2025, 6, 4));

        assertEquals("CUMPLIDA", result.getEstado());
        assertEquals(LocalDate.of(2025, 6, 4), result.getFechaSalidaReal());
        assertEquals(LocalDate.of(2025, 6, 4), result.getFechaSalida());
    }

    @Test
    void checkOut_lanzaExcepcion_cuandoReservaYaEstaCumplida() {
        Reserva r = new Reserva();
        r.setEstado("CUMPLIDA");
        r.setFechaEntrada(LocalDate.of(2025, 6, 1));
        r.setFechaSalida(LocalDate.of(2025, 6, 5));
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(r));

        assertThrows(IllegalArgumentException.class, () -> reservaService.registrarCheckOut(1L, null));
    }

    @Test
    void checkOut_lanzaExcepcion_cuandoReservaNoEstaEnEstadia() {
        Reserva r = new Reserva();
        r.setEstado("ACTIVA");
        r.setFechaEntrada(LocalDate.of(2025, 6, 1));
        r.setFechaSalida(LocalDate.of(2025, 6, 5));
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(r));

        assertThrows(IllegalArgumentException.class, () -> reservaService.registrarCheckOut(1L, null));
    }

    // ── borrar ─────────────────────────────────────────────────────────────

    @Test
    void borrar_lanzaExcepcion_cuandoReservaNoExiste() {
        when(reservaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> reservaService.borrar(99L));
    }

    @Test
    void borrar_delegaEnRepositorio_cuandoReservaExiste() {
        Reserva r = new Reserva();
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(r));

        reservaService.borrar(1L);

        verify(reservaRepository, times(1)).delete(r);
    }
}
