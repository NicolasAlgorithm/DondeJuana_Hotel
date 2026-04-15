package com.project.hotel.service;

import com.project.hotel.entities.TipoHabitacion;
import com.project.hotel.repository.TipoHabitacionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TipoHabitacionService {

    private final TipoHabitacionRepository tipoHabitacionRepository;
    private final InputSanitizer inputSanitizer;

    public TipoHabitacionService(TipoHabitacionRepository tipoHabitacionRepository, InputSanitizer inputSanitizer) {
        this.tipoHabitacionRepository = tipoHabitacionRepository;
        this.inputSanitizer = inputSanitizer;
    }

    public List<TipoHabitacion> listarTodos() {
        return tipoHabitacionRepository.findAll();
    }

    public Optional<TipoHabitacion> buscarPorId(Long id) {
        return tipoHabitacionRepository.findById(id);
    }

    public TipoHabitacion guardar(TipoHabitacion tipoHabitacion) {
        sanitizeTipoHabitacion(tipoHabitacion);
        return tipoHabitacionRepository.save(tipoHabitacion);
    }

    public void eliminar(Long id) {
        tipoHabitacionRepository.deleteById(id);
    }

    private void sanitizeTipoHabitacion(TipoHabitacion tipoHabitacion) {
        String nombre = inputSanitizer.sanitizePlainText(tipoHabitacion.getNombre());
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del tipo de habitacion es obligatorio");
        }
        tipoHabitacion.setNombre(nombre);

        String descripcion = inputSanitizer.sanitizePlainText(tipoHabitacion.getDescripcion());
        tipoHabitacion.setDescripcion((descripcion == null || descripcion.isBlank()) ? null : descripcion);
    }
}
