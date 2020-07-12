package infrastructure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.Optional;

public class App {
    private static final String AWS_S3_BUCKET = "it-di-io-bucket";
    private static final String AWS_SQS_QUEUE = "it-di-io-queue";
    private static final String ORGANIZATION = "IT-DI-IO";
    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        setupBucket();
        String queueUrl = setupQueue();
        setupBucketEventToQueue(queueUrl);
    }

    private static void setupBucketEventToQueue(String queueUrl) {
        S3Client s3Client = S3Client
                .builder()
                .region(Region.EU_CENTRAL_1)
                .build();
        SqsClient sqsClient = SqsClient
                .builder()
                .region(Region.EU_CENTRAL_1)
                .build();
        GetQueueAttributesResponse queueAttributes = sqsClient
                .getQueueAttributes(GetQueueAttributesRequest
                        .builder()
                        .queueUrl(queueUrl)
                        .attributeNames(QueueAttributeName.QUEUE_ARN)
                        .build());
        String queueArn = queueAttributes
                .attributes()
                .get(QueueAttributeName.QUEUE_ARN);
        LOG.info("Queue ARN: {}", queueArn);
        s3Client
                .putBucketNotificationConfiguration(PutBucketNotificationConfigurationRequest
                        .builder()
                        .bucket(AWS_S3_BUCKET)
                        .notificationConfiguration(NotificationConfiguration
                                .builder()
                                .queueConfigurations(QueueConfiguration
                                        .builder()
                                        .events(Event.S3_OBJECT_CREATED)
                                        .queueArn(queueArn)
                                        .build())
                                .build())
                        .build());
    }

    private static String setupQueue() {
        SqsClient sqsClient = SqsClient
                .builder()
                .region(Region.EU_CENTRAL_1)
                .build();
        Optional<String> queueUrl = sqsClient
                .listQueues(ListQueuesRequest
                        .builder()
                        .queueNamePrefix(AWS_SQS_QUEUE)
                        .build())
                .queueUrls()
                .stream()
                .filter(q -> q.endsWith(AWS_SQS_QUEUE))
                .findFirst();
        if (queueUrl.isPresent()) {
            LOG.info("Queue {} already exists, queue URL: {}", AWS_SQS_QUEUE, queueUrl.get());
            try {
                sqsClient
                        .purgeQueue(PurgeQueueRequest
                                .builder()
                                .queueUrl(queueUrl.get())
                                .build());
            } catch (PurgeQueueInProgressException exception) {
                LOG.info("Queue {} has already been purged", queueUrl.get());
            }
            return queueUrl.get();
        }
        return null;
    }

    private static void setupBucket() {
        S3Client s3Client = S3Client
                .builder()
                .region(Region.EU_CENTRAL_1)
                .build();
        Optional<Bucket> bucket = s3Client
                .listBuckets()
                .buckets()
                .stream()
                .filter(b -> b.name().equals(AWS_S3_BUCKET))
                .findFirst();
        if (bucket.isPresent()) {
            LOG.info("Bucket exists");
            ListObjectsResponse objects = s3Client.listObjects(ListObjectsRequest
                    .builder()
                    .bucket(AWS_S3_BUCKET)
                    .build());
            for (S3Object object : objects.contents()) {
                LOG.info("Delete object {}", object.key());
                s3Client.deleteObject(DeleteObjectRequest
                        .builder()
                        .bucket(AWS_S3_BUCKET)
                        .key(object.key())
                        .build());
            }
        } else {
            s3Client
                    .createBucket(CreateBucketRequest
                            .builder()
                            .bucket(AWS_S3_BUCKET)
                            .build());
        }
        s3Client
                .putBucketTagging(PutBucketTaggingRequest
                        .builder()
                        .bucket(AWS_S3_BUCKET)
                        .tagging(Tagging
                                .builder()
                                .tagSet(Tag.builder().key("organization").value(ORGANIZATION).build())
                                .build())
                        .build());

    }
}
