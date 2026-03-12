package com.project.hotel.service;

import com.project.hotel.entities.Reserva;
import com.project.hotel.repository.ReservaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;

    public ReservaService(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    public List<Reserva> listarTodos() {
        return reservaRepository.findAll();
    }

    public Optional<Reserva> buscarPorId(Long id) {
        return reservaRepository.findById(id);
    }

    public Reserva guardar(Reserva reserva) {
        return reservaRepository.save(reserva);
    }

    public void eliminar(Long id) {
        reservaRepository.deleteById(id);
    }

    public List<Reserva> listarPorPersona(Long idPersona) {
        return reservaRepository.findByPersona_IdPersona(idPersona);
    }

    public List<Reserva> listarPorEstado(String estado) {
        return reservaRepository.findByEstado(estado);
    }
}
