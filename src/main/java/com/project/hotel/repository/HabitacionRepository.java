package com.project.hotel.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.hotel.entities.Habitacion;

public interface HabitacionRepository extends JpaRepository<Habitacion, Long> {
    Optional<Habitacion> findByNumero(String numero);
    List<Habitacion> findByEstado(String estado);

    // ── Filtros para el calendario ──────────────────────────────────────────

    /** Todas las habitaciones activas, sin filtros adicionales. */
    List<Habitacion> findByActivo(String activo);

    /** Habitaciones activas de un tipo concreto. */
    List<Habitacion> findByTipoHabitacion_IdTipoAndActivo(Long idTipo, String activo);

    /** Habitaciones activas de un piso concreto. */
    List<Habitacion> findByPisoAndActivo(Integer piso, String activo);

    /** Habitaciones activas de un tipo y piso concretos. */
    List<Habitacion> findByTipoHabitacion_IdTipoAndPisoAndActivo(Long idTipo, Integer piso, String activo);
}