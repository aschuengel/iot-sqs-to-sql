package com.heidelberg;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class Routing extends RouteBuilder {

    @Override
    public void configure() {
        getContext().setTracing(true);
        // @formatter:off
        from("timer:start?period=30000")
                .bean(IotMessageFetcher.class)
                .split(body())
                .to("direct:fetch-file");

        from("direct:fetch-file")
                .bean(IotFileFetcher.class)
                .setHeader("id", simple("${body.message.id}"))
                .setHeader(Exchange.FILE_NAME).simple("${body.message.fileId}")
                .setBody(simple("${body.body}"))
                .to("direct:dispatch-file");

        from("direct:dispatch-file")
                .to("file:upload")
                .to("sftp:localhost:2222?username=foo&password=P@ssw0rd")
                .setBody(simple("${headers:id}"))
                .bean(IotMessageDeleter.class);
        // @formatter:on
    }
}