package com.project.hotel.service.impl;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.hotel.dto.HabitacionRequest;
import com.project.hotel.entities.Habitacion;
import com.project.hotel.repository.HabitacionRepository;
import com.project.hotel.service.HabitacionService;
import com.project.hotel.service.InputSanitizer;

@Service
@Transactional
public class HabitacionServiceImpl implements HabitacionService {

    private static final Set<String> ESTADOS_VALIDOS =
            Set.of("DISPONIBLE", "RESERVADA", "OCUPADA", "MANTENIMIENTO");

    private final HabitacionRepository habitacionRepository;
    private final InputSanitizer inputSanitizer;

    public HabitacionServiceImpl(HabitacionRepository habitacionRepository, InputSanitizer inputSanitizer) {
        this.habitacionRepository = habitacionRepository;
        this.inputSanitizer = inputSanitizer;
    }

    @Override
    public Habitacion crear(HabitacionRequest request) {
        String codigo = inputSanitizer.sanitizeUpperCode(request.getCodigo());
        String numero = inputSanitizer.sanitizeUpperCode(request.getNumero());
        String estado = inputSanitizer.sanitizeUpperCode(request.getEstado());

        validarEstado(estado);

        habitacionRepository.findByNumero(numero)
                .ifPresent(h -> {
                    throw new IllegalArgumentException(
                    "Ya existe una habitacion con numero: " + numero
                    );
                });

        Habitacion h = new Habitacion();
        h.setCodigo(codigo);
        h.setNumero(numero);
        h.setPiso(request.getPiso());
        h.setIdTipoHabitacion(request.getIdTipoHabitacion());
        h.setTarifaNoche(request.getTarifaNoche());
        h.setEstado(estado);
        h.setActivo("S");

        return habitacionRepository.save(h);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Habitacion> listar() {
        return habitacionRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Habitacion> listarTodos() {
        return habitacionRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Habitacion> listarDisponibles() {
        return habitacionRepository.findByEstado("DISPONIBLE");
    }

    @Override
    @Transactional(readOnly = true)
    public Habitacion obtenerPorId(Long id) {
        return habitacionRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Habitacion no encontrada con id: " + id));
    }

    @Override
    public Habitacion actualizar(Long id, HabitacionRequest request) {
        String codigo = inputSanitizer.sanitizeUpperCode(request.getCodigo());
        String numero = inputSanitizer.sanitizeUpperCode(request.getNumero());
        String estado = inputSanitizer.sanitizeUpperCode(request.getEstado());

        validarEstado(estado);

        Habitacion actual = obtenerPorId(id);

        habitacionRepository.findByNumero(numero)
                .ifPresent(h -> {
                    if (!h.getIdHabitacion().equals(id)) {
                        throw new IllegalArgumentException(
                                "Ya existe otra habitacion con numero: " + numero
                        );
                    }
                });

        actual.setCodigo(codigo);
        actual.setNumero(numero);
        actual.setPiso(request.getPiso());
        actual.setIdTipoHabitacion(request.getIdTipoHabitacion());
        actual.setTarifaNoche(request.getTarifaNoche());
        actual.setEstado(estado);

        return habitacionRepository.save(actual);
    }

    @Override
    public Habitacion actualizarEstado(Long id, String estado) {
        String sanitizedEstado = inputSanitizer.sanitizeUpperCode(estado);
        validarEstado(sanitizedEstado);

        Habitacion actual = obtenerPorId(id);
        actual.setEstado(sanitizedEstado);

        return habitacionRepository.save(actual);
    }

    @Override
    public void eliminar(Long id) {
        Habitacion actual = obtenerPorId(id);
        habitacionRepository.delete(actual);
    }

    private void validarEstado(String estado) {
        if (estado == null || !ESTADOS_VALIDOS.contains(estado.toUpperCase())) {
            throw new IllegalArgumentException(
                    "Estado invalido. Valores permitidos: DISPONIBLE, RESERVADA, OCUPADA, MANTENIMIENTO"
            );
        }
    }
}