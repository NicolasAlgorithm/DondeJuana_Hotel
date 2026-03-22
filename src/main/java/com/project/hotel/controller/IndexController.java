package com.project.hotel.controller;

import com.project.hotel.service.DbStatusService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    private final DbStatusService dbStatusService;

    public IndexController(DbStatusService dbStatusService) {
        this.dbStatusService = dbStatusService;
    }

    @GetMapping("/")
    public String index(Model model) {
        String dbUser = dbStatusService.getConnectedUser();
        if (dbUser != null) {
            model.addAttribute("dbOk", true);
            model.addAttribute("dbUser", dbUser);
        } else {
            model.addAttribute("dbOk", false);
        }
        return "index";
    }
}
