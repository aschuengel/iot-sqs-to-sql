package com.heidelberg;

import org.apache.camel.Handler;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public class IotMessageFetcher {
    private final JdbcTemplate jdbcTemplate;
    private final BeanPropertyRowMapper<IotMessage> propertyRowMapper
            = new BeanPropertyRowMapper<>(IotMessage.class);

    public IotMessageFetcher(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Handler
    public List<IotMessage> fetch() {
        return jdbcTemplate.query("SELECT * FROM IotMessage", propertyRowMapper);
    }
}
