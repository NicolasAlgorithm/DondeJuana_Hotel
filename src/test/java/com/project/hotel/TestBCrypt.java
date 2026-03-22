package com.project.hotel;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestBCrypt {
    
    public static void main(String[] args) {
        String raw = "admin123";
        String hashFromDb = "$2a$10$N4.DkAzFcLXC.Jubv1XTuuPXvHQVkuRnPJHqDqk3qHjcjQKlX4TPG";
        System.out.println("MATCHES=" + new BCryptPasswordEncoder().matches(raw, hashFromDb));
    }
}