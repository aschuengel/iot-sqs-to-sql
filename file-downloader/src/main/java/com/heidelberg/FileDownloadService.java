package com.heidelberg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

/**
 * Service to download files from AWS S3
 */
@Service
public class FileDownloadService {
    private final Logger logger = LoggerFactory.getLogger(FileDownloadService.class);

    @Value("${aws.s3.bucket}")
    private String bucket;

    // TODO: Replace by direct download?
    public void download(WorkListEntry message) {
        logger.info("Work list id {}", message.getId());
        if (message.getFilename() == null) {
            throw new NullPointerException("Filename must not be null");
        }
        logger.info("Filename {}", message.getFilename());
        S3Client client = S3Client.builder().region(Region.EU_CENTRAL_1).build();
        ResponseBytes<GetObjectResponse> bytes = client.getObject(
                GetObjectRequest.builder().bucket(bucket).key(message.getFilename()).build(),
                ResponseTransformer.toBytes());
    }
}
