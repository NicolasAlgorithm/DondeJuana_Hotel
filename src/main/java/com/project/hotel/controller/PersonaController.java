package com.project.hotel.controller;

import com.project.hotel.entities.Persona;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class PersonaController {

    @GetMapping("/")
    public String index(Model model) {
        Persona persona1 = new Persona();
        Persona persona2 = new Persona();
        Persona persona3 = new Persona();

        persona1.setIdPersona("001");
        persona1.setNombre("Johan");
        persona1.setApellido("Brito");
        persona1.setEmail("jsaltarinb@ucentral.edu.co");
        persona1.setTelefono("3001002003");

        persona2.setIdPersona("002");
        persona2.setNombre("Nicolas");
        persona2.setApellido("Trujillo");
        persona2.setEmail("ntrujilloc@ucentral.edu.co");
        persona2.setTelefono("3001002004");

        persona3.setIdPersona("003");
        persona3.setNombre("Amparo");
        persona3.setApellido("Grizales");
        persona3.setEmail("marucha@ucentral.edu.co");
        persona3.setTelefono("3001002005");

        List<Persona> personas = new ArrayList<>();
        personas.add(persona1);
        personas.add(persona2);
        personas.add(persona3);

        model.addAttribute("personas", personas);
        return "index";
    }
}