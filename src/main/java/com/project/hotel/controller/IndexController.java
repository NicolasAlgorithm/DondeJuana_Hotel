package com.project.hotel.controller;

import com.project.hotel.service.DbStatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    private static final Logger log = LoggerFactory.getLogger(IndexController.class);

    private final DbStatusService dbStatusService;

    @Value("${spring.jpa.properties.hibernate.default_schema:}")
    private String defaultSchema;

    public IndexController(DbStatusService dbStatusService) {
        this.dbStatusService = dbStatusService;
    }

    @GetMapping("/")
    public String index(Model model) {
        try {
            String dbUser = dbStatusService.getConnectedUser();
            model.addAttribute("dbStatus", "OK (USER=" + dbUser + ", DEFAULT_SCHEMA=" + defaultSchema + ")");
            model.addAttribute("dbOk", true);
        } catch (Exception e) {
            log.error("DB connectivity check failed", e);
            model.addAttribute("dbStatus", "ERROR: no se pudo conectar a la base de datos");
            model.addAttribute("dbOk", false);
        }
        return "index";
    }
}
