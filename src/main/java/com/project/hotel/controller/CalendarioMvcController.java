package com.project.hotel.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/calendario")
public class CalendarioMvcController {

    @GetMapping
    public String verCalendario() {
        return "calendario/interactivo";
    }
}
