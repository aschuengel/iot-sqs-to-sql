package com.heidelberg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

@SpringBootApplication
public class CloudTriggerApplication {
    public static void main(String[] args) {
        SpringApplication.run(CloudTriggerApplication.class, args);
    }

    @Bean(name = "sqsClient")
    public SqsClient sqsClient() {
        SqsClient client = SqsClient.builder().region(Region.EU_CENTRAL_1).build();
        return client;
    }
}