package com.heidelberg;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

@Component
public class Routing extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        // @formatter:off
        from("aws2-sqs://it-di-io-queue?delay=5000&maxMessagesPerPoll=5")
            .doTry()
                .unmarshal().json(JsonLibrary.Jackson, IotMessage.class)
                .bean(IotMessageProcessor.class)
                .bean(IotInsertProcessor.class)
                .marshal().json(JsonLibrary.Jackson)
                .log("${body}")
            .doCatch(Exception.class)
                .log(LoggingLevel.ERROR, "${exception}")
                .stop()
            .end();
        // @formatter:on
    }
}
