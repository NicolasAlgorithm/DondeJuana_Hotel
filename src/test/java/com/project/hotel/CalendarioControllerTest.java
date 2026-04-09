package com.project.hotel;

import com.project.hotel.controller.CalendarioController;
import com.project.hotel.dto.CalendarioResponse;
import com.project.hotel.dto.HabitacionCalendarioDTO;
import com.project.hotel.service.CalendarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CalendarioControllerTest {

    @Mock
    private CalendarioService calendarioService;

    @InjectMocks
    private CalendarioController calendarioController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ── respuesta 200 ────────────────────────────────────────────────────────

    @Test
    void obtenerCalendario_retorna200_cuandoParametrosSonValidos() {
        CalendarioResponse mockResp = buildMockResponse();
        when(calendarioService.obtenerCalendario(any(), any(), isNull(), isNull()))
                .thenReturn(mockResp);

        ResponseEntity<?> resp = calendarioController.obtenerCalendario(
                LocalDate.of(2026, 4, 1),
                LocalDate.of(2026, 4, 8),
                null, null);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertSame(mockResp, resp.getBody());
    }

    @Test
    void obtenerCalendario_pasaFiltroTipo_alServicio() {
        when(calendarioService.obtenerCalendario(any(), any(), eq(3L), isNull()))
                .thenReturn(buildMockResponse());

        calendarioController.obtenerCalendario(
                LocalDate.of(2026, 4, 1),
                LocalDate.of(2026, 4, 8),
                3L, null);

        verify(calendarioService, times(1))
                .obtenerCalendario(
                        eq(LocalDate.of(2026, 4, 1)),
                        eq(LocalDate.of(2026, 4, 8)),
                        eq(3L),
                        isNull());
    }

    @Test
    void obtenerCalendario_pasaFiltroPiso_alServicio() {
        when(calendarioService.obtenerCalendario(any(), any(), isNull(), eq(2)))
                .thenReturn(buildMockResponse());

        calendarioController.obtenerCalendario(
                LocalDate.of(2026, 4, 1),
                LocalDate.of(2026, 4, 8),
                null, 2);

        verify(calendarioService, times(1))
                .obtenerCalendario(any(), any(), isNull(), eq(2));
    }

    // ── respuesta 400 ────────────────────────────────────────────────────────

    @Test
    @SuppressWarnings("unchecked")
    void obtenerCalendario_retorna400_cuandoServicioLanzaIllegalArgument() {
        when(calendarioService.obtenerCalendario(any(), any(), any(), any()))
                .thenThrow(new IllegalArgumentException("fechaInicio debe ser anterior a fechaFin"));

        ResponseEntity<?> resp = calendarioController.obtenerCalendario(
                LocalDate.of(2026, 4, 10),
                LocalDate.of(2026, 4, 5),
                null, null);

        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        Map<String, String> body = (Map<String, String>) resp.getBody();
        assertNotNull(body);
        assertTrue(body.get("error").contains("fechaInicio"));
    }

    // ── respuesta 500 ────────────────────────────────────────────────────────

    @Test
    @SuppressWarnings("unchecked")
    void obtenerCalendario_retorna500_cuandoServicioLanzaExcepcionInesperada() {
        when(calendarioService.obtenerCalendario(any(), any(), any(), any()))
                .thenThrow(new RuntimeException("error de base de datos"));

        ResponseEntity<?> resp = calendarioController.obtenerCalendario(
                LocalDate.of(2026, 4, 1),
                LocalDate.of(2026, 4, 8),
                null, null);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
        Map<String, String> body = (Map<String, String>) resp.getBody();
        assertNotNull(body);
        assertTrue(body.get("error").contains("Error interno"));
    }

    // ── helper ───────────────────────────────────────────────────────────────

    private CalendarioResponse buildMockResponse() {
        CalendarioResponse r = new CalendarioResponse();
        r.setFechaInicio(LocalDate.of(2026, 4, 1));
        r.setFechaFin(LocalDate.of(2026, 4, 8));
        r.setTotalDias(7);
        r.setTotalHabitaciones(0);
        r.setHabitaciones(Collections.emptyList());
        return r;
    }
}
