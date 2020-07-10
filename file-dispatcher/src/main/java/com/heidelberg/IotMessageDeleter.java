package com.heidelberg;

import org.apache.camel.Handler;
import org.springframework.jdbc.core.JdbcTemplate;

public class IotMessageDeleter {
    private final JdbcTemplate jdbcTemplate;

    public IotMessageDeleter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Handler
    public void delete(String id) {
        jdbcTemplate.update("DELETE FROM IoTMessage WHERE Id = ?", id);
    }
}
