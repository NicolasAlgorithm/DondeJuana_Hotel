package com.project.hotel.controller;

import java.math.BigDecimal;

import com.project.hotel.dto.HabitacionRequest;
import com.project.hotel.entities.Habitacion;
import com.project.hotel.service.HabitacionService;
import com.project.hotel.service.TipoHabitacionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/habitaciones")
public class HabitacionMvcController {

    private static final String[] ESTADOS = {"DISPONIBLE", "RESERVADA", "OCUPADA", "MANTENIMIENTO"};

    private final HabitacionService habitacionService;
    private final TipoHabitacionService tipoHabitacionService;

    public HabitacionMvcController(HabitacionService habitacionService,
                                   TipoHabitacionService tipoHabitacionService) {
        this.habitacionService = habitacionService;
        this.tipoHabitacionService = tipoHabitacionService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("habitaciones", habitacionService.listar());
        model.addAttribute("estados", ESTADOS);
        return "habitaciones/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("habitacion", new Habitacion());
        model.addAttribute("tipos", tipoHabitacionService.listarTodos());
        model.addAttribute("estados", ESTADOS);
        return "habitaciones/formulario";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Habitacion habitacion = habitacionService.obtenerPorId(id);
        model.addAttribute("habitacion", habitacion);
        model.addAttribute("tipos", tipoHabitacionService.listarTodos());
        model.addAttribute("estados", ESTADOS);
        return "habitaciones/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Habitacion habitacion, Model model) {
        try {
            HabitacionRequest request = new HabitacionRequest();
            request.setCodigo(habitacion.getDescripcion());
            request.setNumero(habitacion.getNumero());
            request.setPiso(habitacion.getPiso());
            request.setIdTipoHabitacion(habitacion.getIdTipoHabitacion());
            request.setEstado(habitacion.getEstado() != null ? habitacion.getEstado() : "DISPONIBLE");

            if (habitacion.getIdHabitacion() != null) {
                Habitacion existente = habitacionService.obtenerPorId(habitacion.getIdHabitacion());
                request.setTarifaNoche(existente.getTarifaNoche() != null
                        ? existente.getTarifaNoche() : BigDecimal.ZERO);
                habitacionService.actualizar(habitacion.getIdHabitacion(), request);
            } else {
                request.setTarifaNoche(BigDecimal.ZERO);
                habitacionService.crear(request);
            }
            return "redirect:/habitaciones";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("habitacion", habitacion);
            model.addAttribute("tipos", tipoHabitacionService.listarTodos());
            model.addAttribute("estados", ESTADOS);
            return "habitaciones/formulario";
        }
    }

    @PostMapping("/{id}/estado")
    public String cambiarEstado(@PathVariable Long id,
                                @RequestParam String estado) {
        habitacionService.actualizarEstado(id, estado);
        return "redirect:/habitaciones";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        try {
            habitacionService.eliminar(id);
        } catch (IllegalArgumentException e) {
            // Ignore and redirect
        }
        return "redirect:/habitaciones";
    }
}
