package com.project.hotel.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class DbHealthService {

    private static final Logger log = LoggerFactory.getLogger(DbHealthService.class);

    private final JdbcTemplate jdbcTemplate;

    public DbHealthService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Returns the current DB user (e.g. "HOTEL") if the connection is healthy,
     * or null if the connection fails.
     */
    public String getConnectedUser() {
        try {
            return jdbcTemplate.queryForObject("SELECT USER FROM dual", String.class);
        } catch (Exception e) {
            log.error("Database health check failed: {}", e.getMessage(), e);
            return null;
        }
    }

    public boolean isHealthy() {
        return getConnectedUser() != null;
    }
}
