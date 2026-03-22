package com.project.hotel;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GenHash {
    public static void main(String[] args) {
        String raw = "12345";
        System.out.println("RAW=[" + raw + "]");
        System.out.println(new BCryptPasswordEncoder().encode(raw));
    }
}