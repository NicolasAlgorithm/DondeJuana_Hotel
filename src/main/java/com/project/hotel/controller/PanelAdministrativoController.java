package com.project.hotel.controller;

import com.project.hotel.service.PanelAdministrativoService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequestMapping("/panel-administrativo")
public class PanelAdministrativoController {

    private static final String ROLE_ADMIN = "ROLE_ADMINISTRADOR";

    private final PanelAdministrativoService panelAdministrativoService;

    public PanelAdministrativoController(PanelAdministrativoService panelAdministrativoService) {
        this.panelAdministrativoService = panelAdministrativoService;
    }

    @GetMapping
    public String verPanel(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            Authentication authentication,
            Model model
    ) {
        LocalDate fechaBase = fecha != null ? fecha : LocalDate.now();
        boolean esAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> ROLE_ADMIN.equals(a.getAuthority()));

        model.addAttribute("esAdmin", esAdmin);
        model.addAttribute("fechaConsulta", fechaBase);
        model.addAttribute("reporteDiario", panelAdministrativoService.reporteDiario(fechaBase));

        if (esAdmin) {
            model.addAttribute("reporteSemanal", panelAdministrativoService.reporteSemanal(fechaBase));
            model.addAttribute("reporteMensual", panelAdministrativoService.reporteMensual(fechaBase));
        }

        return "panel-administrativo/ocupacion-ingresos";
    }
}
