package com.heidelberg;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

@Component
public class TestRouting extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        // @formatter:off
        /*
        from("timer:foo")
            .bean(FakeMessageGenerator.class)
            .bean(IotMessageProcessor.class)
            .bean(IotInsertProcessor.class)
            .marshal().json(JsonLibrary.Jackson)
            .to("log:bar")
            .to("file:logs");
        */
        from("aws2-sqs://test?delay=5000&maxMessagesPerPoll=5")
            .doTry()
                .unmarshal().json(JsonLibrary.Jackson, IotMessage.class)
                .bean(IotMessageProcessor.class)
                .bean(IotInsertProcessor.class)
                .marshal().json(JsonLibrary.Jackson)
                .to("log:bar")
                .to("file:logs")
            .doCatch(Exception.class)
                .to("log:error?level=ERROR")
                .stop()
            .end();
        // @formatter:on
    }
}
