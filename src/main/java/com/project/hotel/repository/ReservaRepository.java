package com.project.hotel.repository;

import com.project.hotel.entities.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findByPersona_IdPersona(Long idPersona);

    List<Reserva> findByEstado(String estado);
}
