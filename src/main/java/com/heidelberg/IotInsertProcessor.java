package com.heidelberg;

import org.apache.camel.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

public class IotInsertProcessor {
    private final JdbcTemplate jdbcTemplate;
    private final Logger logger = LoggerFactory.getLogger(IotInsertProcessor.class);

    @Handler
    public IotMessage insert(IotMessage message) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate);
        insert.setTableName("IotMessage");
        insert.execute(new BeanPropertySqlParameterSource(message));
        logger.info("Inserted message of type {} with timestamp {}", message.getType(), message.getDate());
        return message;
    }

    public IotInsertProcessor(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
