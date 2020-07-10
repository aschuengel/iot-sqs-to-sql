package com.heidelberg;

import org.apache.camel.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

public class IotFileFetcher {
    private final Logger logger = LoggerFactory.getLogger(IotFileFetcher.class);

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Handler
    public IotFile process(IotMessage message) {
        logger.info("Message id {}", message.getId());
        logger.info("File id {}", message.getFileId());
        if (message.getFileId() == null) {
            throw new NullPointerException("File id must not be null");
        }
        S3Client client = S3Client.builder().region(Region.EU_CENTRAL_1).build();
        ResponseBytes<GetObjectResponse> bytes = client.getObject(
                GetObjectRequest.builder().bucket(bucket).key(message.getFileId()).build(),
                ResponseTransformer.toBytes());
        IotFile file = new IotFile();
        file.setMessage(message);
        file.setBody(bytes.asByteArray());
        return file;
    }
}
