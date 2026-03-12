package com.project.hotel.service;

import com.project.hotel.entities.TipoHabitacion;
import com.project.hotel.repository.TipoHabitacionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TipoHabitacionService {

    private final TipoHabitacionRepository tipoHabitacionRepository;

    public TipoHabitacionService(TipoHabitacionRepository tipoHabitacionRepository) {
        this.tipoHabitacionRepository = tipoHabitacionRepository;
    }

    public List<TipoHabitacion> listarTodos() {
        return tipoHabitacionRepository.findAll();
    }

    public Optional<TipoHabitacion> buscarPorId(Long id) {
        return tipoHabitacionRepository.findById(id);
    }

    public TipoHabitacion guardar(TipoHabitacion tipoHabitacion) {
        return tipoHabitacionRepository.save(tipoHabitacion);
    }

    public void eliminar(Long id) {
        tipoHabitacionRepository.deleteById(id);
    }
}
