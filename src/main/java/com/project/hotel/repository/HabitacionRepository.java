package com.project.hotel.repository;

import com.project.hotel.model.Habitacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HabitacionRepository extends JpaRepository<Habitacion, Long> {
    Optional<Habitacion> findByCodigo(String codigo);
}