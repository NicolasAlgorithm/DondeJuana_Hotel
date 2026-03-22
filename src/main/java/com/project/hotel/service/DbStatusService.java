package com.project.hotel.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class DbStatusService {

    private static final Logger log = LoggerFactory.getLogger(DbStatusService.class);

    private final JdbcTemplate jdbcTemplate;

    public DbStatusService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Returns the DB user name if connection is healthy, or null if an error occurs.
     */
    public String getConnectedUser() {
        try {
            return jdbcTemplate.queryForObject("SELECT user FROM dual", String.class);
        } catch (DataAccessException e) {
            log.error("DB connectivity check failed: {}", e.getMessage());
            return null;
        }
    }
}
