package com.project.hotel.service;

import java.util.List;

import com.project.hotel.dto.HabitacionRequest;
import com.project.hotel.entities.Habitacion;

public interface HabitacionService {
    Habitacion crear(HabitacionRequest request);
    List<Habitacion> listar();
    List<Habitacion> listarTodos();
    List<Habitacion> listarDisponibles();
    Habitacion obtenerPorId(Long id);
    Habitacion actualizar(Long id, HabitacionRequest request);
    Habitacion actualizarEstado(Long id, String estado);
}