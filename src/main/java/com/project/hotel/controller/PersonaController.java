package com.project.hotel.controller;

import com.project.hotel.entities.Persona;
import com.project.hotel.service.PersonaService;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/personas")
public class PersonaController {

    private final PersonaService personaService;

    public PersonaController(PersonaService personaService) {
        this.personaService = personaService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("personas", personaService.listarTodos());
        return "personas/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("persona", new Persona());
        return "personas/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute Persona persona, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "personas/formulario";
        }

        try {
            personaService.guardar(persona);
            return "redirect:/personas";
        } catch (DataIntegrityViolationException e) {
            model.addAttribute("dbError", "No fue posible guardar el huésped. Verifica que el documento no esté repetido y que los datos sean válidos.");
            return "personas/formulario";
        } catch (Exception e) {
            model.addAttribute("dbError", "No fue posible guardar el huésped en este momento. Intenta nuevamente.");
            return "personas/formulario";
        }
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        personaService.buscarPorId(id).ifPresent(p -> model.addAttribute("persona", p));
        return "personas/formulario";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        personaService.eliminar(id);
        return "redirect:/personas";
    }
}
