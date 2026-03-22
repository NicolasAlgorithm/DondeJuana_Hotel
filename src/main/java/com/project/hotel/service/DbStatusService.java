package com.project.hotel.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class DbStatusService {

    private final JdbcTemplate jdbcTemplate;

    public DbStatusService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String getConnectedUser() {
        return jdbcTemplate.queryForObject("SELECT user FROM dual", String.class);
    }
}
