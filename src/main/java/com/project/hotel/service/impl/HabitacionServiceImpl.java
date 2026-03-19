package com.project.hotel.service.impl;

import com.project.hotel.dto.HabitacionRequest;
import com.project.hotel.model.Habitacion;
import com.project.hotel.repository.HabitacionRepository;
import com.project.hotel.service.HabitacionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Transactional
public class HabitacionServiceImpl implements HabitacionService {

    private static final Set<String> ESTADOS_VALIDOS = Set.of("DISPONIBLE", "OCUPADA", "MANTENIMIENTO");
    private final HabitacionRepository habitacionRepository;

    public HabitacionServiceImpl(HabitacionRepository habitacionRepository) {
        this.habitacionRepository = habitacionRepository;
    }

    @Override
    public Habitacion crear(HabitacionRequest request) {
        validarEstado(request.getEstado());

        habitacionRepository.findByCodigo(request.getCodigo())
                .ifPresent(h -> {
                    throw new IllegalArgumentException("Ya existe una habitación con código: " + request.getCodigo());
                });

        Habitacion h = new Habitacion();
        h.setCodigo(request.getCodigo());
        h.setNumero(request.getNumero());
        h.setPiso(request.getPiso());
        h.setIdTipoHabitacion(request.getIdTipoHabitacion());
        h.setTarifaNoche(request.getTarifaNoche());
        h.setEstado(request.getEstado().toUpperCase());
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
    public Habitacion obtenerPorId(Long id) {
        return habitacionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Habitación no encontrada con id: " + id));
    }

    @Override
    public Habitacion actualizar(Long id, HabitacionRequest request) {
        validarEstado(request.getEstado());

        Habitacion actual = obtenerPorId(id);

        habitacionRepository.findByCodigo(request.getCodigo())
                .ifPresent(h -> {
                    if (!h.getIdHabitacion().equals(id)) {
                        throw new IllegalArgumentException("Ya existe otra habitación con código: " + request.getCodigo());
                    }
                });

        actual.setCodigo(request.getCodigo());
        actual.setNumero(request.getNumero());
        actual.setPiso(request.getPiso());
        actual.setIdTipoHabitacion(request.getIdTipoHabitacion());
        actual.setTarifaNoche(request.getTarifaNoche());
        actual.setEstado(request.getEstado().toUpperCase());

        return habitacionRepository.save(actual);
    }

    @Override
    public Habitacion actualizarEstado(Long id, String estado) {
        validarEstado(estado);

        Habitacion actual = obtenerPorId(id);
        actual.setEstado(estado.toUpperCase());

        return habitacionRepository.save(actual);
    }

    private void validarEstado(String estado) {
        if (estado == null || !ESTADOS_VALIDOS.contains(estado.toUpperCase())) {
            throw new IllegalArgumentException("Estado inválido. Valores permitidos: DISPONIBLE, OCUPADA, MANTENIMIENTO");
        }
    }
}