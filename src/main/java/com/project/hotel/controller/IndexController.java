package com.project.hotel.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    private static final Logger log = LoggerFactory.getLogger(IndexController.class);

    private final JdbcTemplate jdbcTemplate;

    public IndexController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/")
    public String index(Model model) {
        try {
            Long userCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM ADMIN.USUARIOS", Long.class);
            model.addAttribute("dbStatus", "OK");
            model.addAttribute("dbDetail", "Conectado · " + userCount + " usuario(s) en BD");
        } catch (Exception e) {
            log.error("DB health check failed", e);
            model.addAttribute("dbStatus", "ERROR");
            model.addAttribute("dbDetail", "No se pudo conectar a la base de datos. Revisa la configuración.");
        }
        return "index";
    }
}
