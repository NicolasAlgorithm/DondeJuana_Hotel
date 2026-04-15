package com.project.hotel;

import com.project.hotel.controller.HabitacionMvcController;
import com.project.hotel.entities.Habitacion;
import com.project.hotel.entities.TipoHabitacion;
import com.project.hotel.service.HabitacionService;
import com.project.hotel.service.TipoHabitacionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class HabitacionMvcControllerTest {

    @Mock
    private HabitacionService habitacionService;
    @Mock
    private TipoHabitacionService tipoHabitacionService;

    @InjectMocks
    private HabitacionMvcController habitacionMvcController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void listar_retornaVistaYModeloConEstados() {
        when(habitacionService.listar()).thenReturn(Collections.emptyList());

        Model model = new ExtendedModelMap();
        String vista = habitacionMvcController.listar(model);

        assertEquals("habitaciones/lista", vista);
        assertEquals(Collections.emptyList(), model.getAttribute("habitaciones"));
    }

    @Test
    void guardar_creaHabitacionNuevaConValoresPorDefectoYRedirige() {
        Habitacion habitacion = habitacionNueva();

        String vista = habitacionMvcController.guardar(habitacion, new ExtendedModelMap());

        assertEquals("redirect:/habitaciones", vista);
        verify(habitacionService).crear(any());
        verify(habitacionService, never()).actualizar(anyLong(), any());
    }

    @Test
    void guardar_actualizaHabitacionExistenteConTarifaActualYRedirige() {
        Habitacion existente = new Habitacion();
        existente.setTarifaNoche(BigDecimal.valueOf(90));
        when(habitacionService.obtenerPorId(1L)).thenReturn(existente);

        Habitacion habitacion = habitacionNueva();
        habitacion.setIdHabitacion(1L);

        String vista = habitacionMvcController.guardar(habitacion, new ExtendedModelMap());

        assertEquals("redirect:/habitaciones", vista);
        verify(habitacionService).actualizar(eq(1L), any());
        verify(habitacionService, never()).crear(any());
    }

    @Test
    void guardar_retornaFormularioConError_siServicioLanzaIllegalArgument() {
        Habitacion habitacion = habitacionNueva();
        when(tipoHabitacionService.listarTodos()).thenReturn(Collections.emptyList());
        doThrow(new IllegalArgumentException("estado inválido")).when(habitacionService).crear(any());

        Model model = new ExtendedModelMap();
        String vista = habitacionMvcController.guardar(habitacion, model);

        assertEquals("habitaciones/formulario", vista);
        assertEquals("estado inválido", model.getAttribute("error"));
    }

    @Test
    void eliminar_ignoraErrorYSiempreRedirige() {
        doThrow(new IllegalArgumentException("no encontrada")).when(habitacionService).eliminar(9L);

        String vista = habitacionMvcController.eliminar(9L);

        assertEquals("redirect:/habitaciones", vista);
    }

    private Habitacion habitacionNueva() {
        Habitacion habitacion = new Habitacion();
        habitacion.setDescripcion("HAB-A");
        habitacion.setNumero("A-10");
        habitacion.setPiso(1);
        TipoHabitacion tipo = new TipoHabitacion();
        tipo.setIdTipo(3L);
        habitacion.setTipoHabitacion(tipo);
        habitacion.setEstado(null);
        return habitacion;
    }
}
