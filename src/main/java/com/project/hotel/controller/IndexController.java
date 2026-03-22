package com.project.hotel.controller;

import com.project.hotel.service.DbHealthService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    private final DbHealthService dbHealthService;

    public IndexController(DbHealthService dbHealthService) {
        this.dbHealthService = dbHealthService;
    }

    @GetMapping("/")
    public String index(Model model) {
        String dbUser = dbHealthService.getConnectedUser();
        model.addAttribute("dbOk", dbUser != null);
        model.addAttribute("dbUser", dbUser);
        return "index";
    }
}
