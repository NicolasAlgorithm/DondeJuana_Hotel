package com.project.hotel.controller;

import com.project.hotel.entities.TipoHabitacion;
import com.project.hotel.service.TipoHabitacionService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/tipos-habitacion")
public class TipoHabitacionController {

    private final TipoHabitacionService tipoHabitacionService;

    public TipoHabitacionController(TipoHabitacionService tipoHabitacionService) {
        this.tipoHabitacionService = tipoHabitacionService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("tipos", tipoHabitacionService.listarTodos());
        return "tipos-habitacion/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("tipo", new TipoHabitacion());
        return "tipos-habitacion/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("tipo") TipoHabitacion tipo, BindingResult result) {
        if (result.hasErrors()) {
            return "tipos-habitacion/formulario";
        }
        tipoHabitacionService.guardar(tipo);
        return "redirect:/tipos-habitacion";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        tipoHabitacionService.buscarPorId(id).ifPresent(t -> model.addAttribute("tipo", t));
        return "tipos-habitacion/formulario";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        tipoHabitacionService.eliminar(id);
        return "redirect:/tipos-habitacion";
    }
}
