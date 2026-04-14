package com.project.hotel.controller;

import com.project.hotel.entities.Reserva;
import com.project.hotel.service.HabitacionService;
import com.project.hotel.service.PersonaService;
import com.project.hotel.service.ReservaService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/reservas")
public class ReservaController {

    private final ReservaService reservaService;
    private final PersonaService personaService;
    private final HabitacionService habitacionService;

    public ReservaController(ReservaService reservaService, PersonaService personaService, HabitacionService habitacionService) {
        this.reservaService = reservaService;
        this.personaService = personaService;
        this.habitacionService = habitacionService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("reservas", reservaService.listarTodos());
        return "reservas/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("reserva", new Reserva());
        model.addAttribute("personas", personaService.listarTodos());
        model.addAttribute("habitaciones", habitacionService.listarDisponibles());
        model.addAttribute("estados", new String[]{"ACTIVA", "EN_ESTADIA", "CANCELADA", "CUMPLIDA"});
        return "reservas/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute Reserva reserva, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("personas", personaService.listarTodos());
            model.addAttribute("habitaciones", habitacionService.listarDisponibles());
            model.addAttribute("estados", new String[]{"ACTIVA", "EN_ESTADIA", "CANCELADA", "CUMPLIDA"});
            return "reservas/formulario";
        }
        reservaService.guardar(reserva);
        return "redirect:/reservas";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Optional<Reserva> reservaOpt = reservaService.buscarPorId(id);
        if (reservaOpt.isEmpty()) {
            return "redirect:/reservas";
        }
        model.addAttribute("reserva", reservaOpt.get());
        model.addAttribute("personas", personaService.listarTodos());
        model.addAttribute("habitaciones", habitacionService.listarTodos());
        model.addAttribute("estados", new String[]{"ACTIVA", "EN_ESTADIA", "CANCELADA", "CUMPLIDA"});
        return "reservas/formulario";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        reservaService.eliminar(id);
        return "redirect:/reservas";
    }
}
