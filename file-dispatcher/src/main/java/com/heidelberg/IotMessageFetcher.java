package com.heidelberg;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.camel.Handler;

public class IotMessageFetcher {
    @Handler
    public List<IotMessage> fetch() {
        return IntStream.range(0, 10).mapToObj(i -> {
            IotMessage message = new IotMessage();
            message.setId(UUID.randomUUID().toString());
            message.setDate(new Date());
            message.setComment("Fake date " + i);
            message.setType("test");
            return message;
        }).collect(Collectors.toList());
    }
}
