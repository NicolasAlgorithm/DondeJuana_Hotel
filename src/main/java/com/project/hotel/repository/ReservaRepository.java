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
 );

 /**
  * Devuelve todas las reservas no canceladas para las habitaciones indicadas
  * que se solapan con el rango [{@code fechaInicio}, {@code fechaFin}).
  *
  * <p>Se usa para construir el calendario: con esta única consulta se obtienen
  * los bloqueos de todas las habitaciones del rango de una sola pasada.</p>
  *
  * @param ids        ids de las habitaciones a consultar
  * @param fechaInicio primer día del rango (incluido)
  * @param fechaFin    último día del rango (excluido)
  * @return lista de reservas solapadas (estado != CANCELADA)
  */
 @Query("""
     select r from Reserva r
     join fetch r.habitacion
     where r.habitacion.idHabitacion in :ids
       and upper(coalesce(r.estado, '')) <> 'CANCELADA'
       and r.fechaEntrada < :fechaFin
       and r.fechaSalida > :fechaInicio
 """)
 List<Reserva> findActivasEnRangoParaHabitaciones(
         @Param("ids") List<Long> ids,
         @Param("fechaInicio") LocalDate fechaInicio,
         @Param("fechaFin") LocalDate fechaFin
 );
}