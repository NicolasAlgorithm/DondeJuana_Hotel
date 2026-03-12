package com.project.hotel.controller;

import com.project.hotel.entities.Habitacion;
import com.project.hotel.service.HabitacionService;
import com.project.hotel.service.TipoHabitacionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/habitaciones")
public class HabitacionController {

    private final HabitacionService habitacionService;
    private final TipoHabitacionService tipoHabitacionService;

    public HabitacionController(HabitacionService habitacionService, TipoHabitacionService tipoHabitacionService) {
        this.habitacionService = habitacionService;
        this.tipoHabitacionService = tipoHabitacionService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("habitaciones", habitacionService.listarTodos());
        return "habitaciones/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("habitacion", new Habitacion());
        model.addAttribute("tipos", tipoHabitacionService.listarTodos());
        model.addAttribute("estados", new String[]{"Disponible", "Ocupada", "Mantenimiento"});
        return "habitaciones/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute Habitacion habitacion, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("tipos", tipoHabitacionService.listarTodos());
            model.addAttribute("estados", new String[]{"Disponible", "Ocupada", "Mantenimiento"});
            return "habitaciones/formulario";
        }
        habitacionService.guardar(habitacion);
        return "redirect:/habitaciones";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        habitacionService.buscarPorId(id).ifPresent(h -> model.addAttribute("habitacion", h));
        model.addAttribute("tipos", tipoHabitacionService.listarTodos());
        model.addAttribute("estados", new String[]{"Disponible", "Ocupada", "Mantenimiento"});
        return "habitaciones/formulario";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        habitacionService.eliminar(id);
        return "redirect:/habitaciones";
    }
}
