package com.heidelberg;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

@Component
public class Routing extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // @formatter:off
        from("timer:start?period=5000")
            .bean(IotMessageFetcher.class)
            .split(body())
            .setHeader("CamelFileName").spel("#{body.id}")
            .marshal().json(JsonLibrary.Jackson)
            .to("sftp:localhost:2222?username=foo&password=P@ssw0rd")
            .to("log:test");
        // @formatter:on
    }
}