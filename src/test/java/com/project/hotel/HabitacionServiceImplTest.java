package com.project.hotel;

import com.project.hotel.dto.HabitacionRequest;
import com.project.hotel.entities.Habitacion;
import com.project.hotel.repository.HabitacionRepository;
import com.project.hotel.service.impl.HabitacionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class HabitacionServiceImplTest {

    @Mock
    private HabitacionRepository habitacionRepository;

    @InjectMocks
    private HabitacionServiceImpl habitacionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void crear_guardaHabitacionConEstadoNormalizadoYActiva() {
        HabitacionRequest request = request("A-101", "101", "disponible");
        when(habitacionRepository.findByNumero("101")).thenReturn(Optional.empty());
        when(habitacionRepository.save(any(Habitacion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Habitacion creada = habitacionService.crear(request);

        assertEquals("DISPONIBLE", creada.getEstado());
        assertEquals("S", creada.getActivo());
        assertEquals("A-101", creada.getCodigo());
    }

    @Test
    void crear_lanzaExcepcion_siNumeroYaExiste() {
        HabitacionRequest request = request("A-101", "101", "DISPONIBLE");
        Habitacion existente = new Habitacion();
        existente.setIdHabitacion(9L);
        when(habitacionRepository.findByNumero("101")).thenReturn(Optional.of(existente));

        assertThrows(IllegalArgumentException.class, () -> habitacionService.crear(request));
        verify(habitacionRepository, never()).save(any());
    }

    @Test
    void actualizar_lanzaExcepcion_siEstadoEsInvalido() {
        HabitacionRequest request = request("A-101", "101", "INVALIDO");

        assertThrows(IllegalArgumentException.class, () -> habitacionService.actualizar(1L, request));
        verify(habitacionRepository, never()).save(any());
    }

    @Test
    void actualizar_lanzaExcepcion_siNumeroPerteneceAOtraHabitacion() {
        HabitacionRequest request = request("B-201", "201", "OCUPADA");

        Habitacion actual = new Habitacion();
        actual.setIdHabitacion(1L);
        when(habitacionRepository.findById(1L)).thenReturn(Optional.of(actual));

        Habitacion conflicto = new Habitacion();
        conflicto.setIdHabitacion(2L);
        when(habitacionRepository.findByNumero("201")).thenReturn(Optional.of(conflicto));

        assertThrows(IllegalArgumentException.class, () -> habitacionService.actualizar(1L, request));
        verify(habitacionRepository, never()).save(any());
    }

    @Test
    void actualizarEstado_actualizaYNormalizaMayusculas() {
        Habitacion actual = new Habitacion();
        actual.setIdHabitacion(1L);
        actual.setEstado("DISPONIBLE");
        when(habitacionRepository.findById(1L)).thenReturn(Optional.of(actual));
        when(habitacionRepository.save(any(Habitacion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Habitacion actualizada = habitacionService.actualizarEstado(1L, "mantenimiento");

        assertEquals("MANTENIMIENTO", actualizada.getEstado());
        verify(habitacionRepository).save(actual);
    }

    @Test
    void eliminar_borraHabitacionCuandoExiste() {
        Habitacion actual = new Habitacion();
        actual.setIdHabitacion(1L);
        when(habitacionRepository.findById(1L)).thenReturn(Optional.of(actual));

        habitacionService.eliminar(1L);

        verify(habitacionRepository).delete(actual);
    }

    @Test
    void listarDisponibles_retornaValoresDelRepositorio() {
        Habitacion h = new Habitacion();
        h.setEstado("DISPONIBLE");
        when(habitacionRepository.findByEstado("DISPONIBLE")).thenReturn(List.of(h));

        List<Habitacion> disponibles = habitacionService.listarDisponibles();

        assertEquals(1, disponibles.size());
        assertEquals("DISPONIBLE", disponibles.get(0).getEstado());
    }

    private HabitacionRequest request(String codigo, String numero, String estado) {
        HabitacionRequest request = new HabitacionRequest();
        request.setCodigo(codigo);
        request.setNumero(numero);
        request.setPiso(1);
        request.setIdTipoHabitacion(1L);
        request.setTarifaNoche(BigDecimal.valueOf(120_000));
        request.setEstado(estado);
        return request;
    }
}
