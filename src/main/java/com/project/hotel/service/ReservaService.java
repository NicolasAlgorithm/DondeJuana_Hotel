package com.project.hotel.service;

import com.project.hotel.entities.Habitacion;
import com.project.hotel.entities.Persona;
import com.project.hotel.entities.Reserva;
import com.project.hotel.repository.HabitacionRepository;
import com.project.hotel.repository.PersonaRepository;
import com.project.hotel.repository.ReservaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ReservaService {

    // Estados reales en BD (constraint SYS_C0033032)
    private static final String ESTADO_ACTIVA = "ACTIVA";
    private static final String ESTADO_CANCELADA = "CANCELADA";
    private static final String ESTADO_CUMPLIDA = "CUMPLIDA";

    private final ReservaRepository reservaRepository;
    private final PersonaRepository personaRepository;
    private final HabitacionRepository habitacionRepository;

    public ReservaService(
            ReservaRepository reservaRepository,
            PersonaRepository personaRepository,
            HabitacionRepository habitacionRepository
    ) {
        this.reservaRepository = reservaRepository;
        this.personaRepository = personaRepository;
        this.habitacionRepository = habitacionRepository;
    }

    @Transactional(readOnly = true)
    public List<Reserva> listarTodos() {
        return reservaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Reserva> buscarPorId(Long id) {
        return reservaRepository.findById(id);
    }

    public Reserva guardar(Reserva reserva) {
        return reservaRepository.save(reserva);
    }

    public void eliminar(Long id) {
        reservaRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Reserva> listarPorPersona(Long idPersona) {
        return reservaRepository.findByPersona_IdPersona(idPersona);
    }

    @Transactional(readOnly = true)
    public List<Reserva> listarPorEstado(String estado) {
        return reservaRepository.findByEstado(normalizarEstado(estado, ESTADO_ACTIVA));
    }

    public Reserva crear(Long idPersona, Long idHabitacion, LocalDate fechaEntrada, LocalDate fechaSalida, String estado) {
        validarFechas(fechaEntrada, fechaSalida);
        validarDisponibilidad(idHabitacion, fechaEntrada, fechaSalida, null);

        Persona persona = personaRepository.findById(idPersona)
                .orElseThrow(() -> new IllegalArgumentException("Persona no encontrada: " + idPersona));

        Habitacion habitacion = habitacionRepository.findById(idHabitacion)
                .orElseThrow(() -> new IllegalArgumentException("Habitación no encontrada: " + idHabitacion));

        Reserva r = new Reserva();
        r.setPersona(persona);
        r.setHabitacion(habitacion);
        r.setFechaEntrada(fechaEntrada);
        r.setFechaSalida(fechaSalida);
        r.setEstado(normalizarEstado(estado, ESTADO_ACTIVA));

        return reservaRepository.save(r);
    }

    public Reserva modificar(Long idReserva, Long idPersona, Long idHabitacion, LocalDate fechaEntrada, LocalDate fechaSalida, String estado) {
        validarFechas(fechaEntrada, fechaSalida);
        validarDisponibilidad(idHabitacion, fechaEntrada, fechaSalida, idReserva);

        Reserva actual = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada: " + idReserva));

        Persona persona = personaRepository.findById(idPersona)
                .orElseThrow(() -> new IllegalArgumentException("Persona no encontrada: " + idPersona));

        Habitacion habitacion = habitacionRepository.findById(idHabitacion)
                .orElseThrow(() -> new IllegalArgumentException("Habitación no encontrada: " + idHabitacion));

        actual.setPersona(persona);
        actual.setHabitacion(habitacion);
        actual.setFechaEntrada(fechaEntrada);
        actual.setFechaSalida(fechaSalida);
        actual.setEstado(normalizarEstado(estado, actual.getEstado()));

        return reservaRepository.save(actual);
    }

    public Reserva cancelar(Long idReserva) {
        Reserva actual = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada: " + idReserva));

        String estadoActual = normalizarEstado(actual.getEstado(), ESTADO_ACTIVA);
        if (ESTADO_CUMPLIDA.equals(estadoActual)) {
            throw new IllegalArgumentException("No se puede cancelar una reserva CUMPLIDA");
        }

        actual.setEstado(ESTADO_CANCELADA);
        return reservaRepository.save(actual);
    }

    public void borrar(Long idReserva) {
        Reserva actual = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada: " + idReserva));
        reservaRepository.delete(actual);
    }

    @Transactional(readOnly = true)
    public boolean estaDisponible(Long idHabitacion, LocalDate fechaEntrada, LocalDate fechaSalida, Long idReservaExcluir) {
        validarFechas(fechaEntrada, fechaSalida);
        return !reservaRepository.existeTraslapeActivo(idHabitacion, fechaEntrada, fechaSalida, idReservaExcluir);
    }

    private void validarDisponibilidad(Long idHabitacion, LocalDate fechaEntrada, LocalDate fechaSalida, Long idReservaExcluir) {
        boolean disponible = estaDisponible(idHabitacion, fechaEntrada, fechaSalida, idReservaExcluir);
        if (!disponible) {
            throw new IllegalArgumentException("La habitación no está disponible en el rango de fechas solicitado");
        }
    }

    private void validarFechas(LocalDate fechaEntrada, LocalDate fechaSalida) {
        if (fechaEntrada == null || fechaSalida == null) {
            throw new IllegalArgumentException("Fecha de entrada y salida son obligatorias");
        }
        if (!fechaEntrada.isBefore(fechaSalida)) {
            throw new IllegalArgumentException("La fecha de entrada debe ser menor que la fecha de salida");
        }
    }

    private String normalizarEstado(String estado, String porDefecto) {
        String valor = (estado == null || estado.isBlank()) ? porDefecto : estado.trim();

        // Acepta valores de UI y los traduce al constraint real de BD
        if (valor.equalsIgnoreCase("ACTIVA") || valor.equalsIgnoreCase("CONFIRMADA") || valor.equalsIgnoreCase("CHECKIN")) {
            return ESTADO_ACTIVA;
        }
        if (valor.equalsIgnoreCase("CANCELADA")) {
            return ESTADO_CANCELADA;
        }
        if (valor.equalsIgnoreCase("CUMPLIDA") || valor.equalsIgnoreCase("CHECKOUT")) {
            return ESTADO_CUMPLIDA;
        }

        throw new IllegalArgumentException("Estado inválido. Valores permitidos en BD: ACTIVA, CANCELADA, CUMPLIDA");
    }
}