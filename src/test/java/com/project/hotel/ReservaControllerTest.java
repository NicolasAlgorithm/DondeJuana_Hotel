package com.project.hotel;

import com.project.hotel.controller.ReservaController;
import com.project.hotel.entities.Habitacion;
import com.project.hotel.entities.Persona;
import com.project.hotel.entities.Reserva;
import com.project.hotel.service.HabitacionService;
import com.project.hotel.service.PersonaService;
import com.project.hotel.service.ReservaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ReservaControllerTest {

    @Mock
    private ReservaService reservaService;
    @Mock
    private PersonaService personaService;
    @Mock
    private HabitacionService habitacionService;

    @InjectMocks
    private ReservaController reservaController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void editar_redireccionaALista_cuandoReservaNoExiste() {
        when(reservaService.buscarPorId(99L)).thenReturn(Optional.empty());

        Model model = new ExtendedModelMap();
        String vista = reservaController.editar(99L, model);

        assertEquals("redirect:/reservas", vista);
        verify(reservaService, times(1)).buscarPorId(99L);
        // listarTodos / listarDisponibles should NOT be called when not found
        verifyNoInteractions(personaService, habitacionService);
    }

    @Test
    void editar_cargaFormulario_cuandoReservaExiste() {
        Reserva reserva = new Reserva();
        reserva.setIdReserva(1L);
        reserva.setFechaEntrada(LocalDate.of(2025, 6, 1));
        reserva.setFechaSalida(LocalDate.of(2025, 6, 5));
        reserva.setEstado("ACTIVA");
        reserva.setPersona(new Persona());
        reserva.setHabitacion(new Habitacion());

        when(reservaService.buscarPorId(1L)).thenReturn(Optional.of(reserva));
        when(personaService.listarTodos()).thenReturn(Collections.emptyList());
        when(habitacionService.listarTodos()).thenReturn(Collections.emptyList());

        Model model = new ExtendedModelMap();
        String vista = reservaController.editar(1L, model);

        assertEquals("reservas/formulario", vista);
        assertEquals(reserva, model.getAttribute("reserva"));
    }

    @Test
    void listar_retornaVistaListaConReservas() {
        when(reservaService.listarTodos()).thenReturn(Collections.emptyList());

        Model model = new ExtendedModelMap();
        String vista = reservaController.listar(model);

        assertEquals("reservas/lista", vista);
    }
}
