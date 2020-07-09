package com.heidelberg;

import org.apache.camel.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.UUID;

public class IotMessageProcessor {
    private final Logger logger = LoggerFactory.getLogger(IotMessageProcessor.class);

    @Handler
    public IotMessage handle(IotMessage message) {
        if (message.getDate() == null) {
            message.setDate(new Date());
            logger.warn("Setting date to current date: {}", message.getDate());
        }
        if (message.getType() == null) {
            logger.error("Message type is null");
            throw new IllegalArgumentException("Message type must not be null");
        }
        message.setId(UUID.randomUUID().toString());
        return message;
    }
}
