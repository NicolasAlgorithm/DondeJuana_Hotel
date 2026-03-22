package com.project.hotel.repository;

import com.project.hotel.entities.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> { List<Reserva> findByPersona_IdPersona(Long idPersona);

 List<Reserva> findByEstado(String estado);

 @Query("""
     select case when count(r) > 0 then true else false end
     from Reserva r
     where r.habitacion.idHabitacion = :idHabitacion
       and upper(coalesce(r.estado, '')) <> 'CANCELADA'
       and (:idReservaExcluir is null or r.idReserva <> :idReservaExcluir)
       and r.fechaEntrada < :fechaSalida
       and r.fechaSalida > :fechaEntrada
 """)
 boolean existeTraslapeActivo(
         @Param("idHabitacion") Long idHabitacion,
         @Param("fechaEntrada") LocalDate fechaEntrada,
         @Param("fechaSalida") LocalDate fechaSalida,
         @Param("idReservaExcluir") Long idReservaExcluir
 );}