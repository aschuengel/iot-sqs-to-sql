package com.heidelberg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

/**
 * Application to download files from AWS S3
 */
@SpringBootApplication
@EnableScheduling
public class FileDownloadApplication {
    private final WorkListService workListService;
    private final FileDownloadService downloadService;
    private final Logger logger = LoggerFactory.getLogger(FileDownloadApplication.class);

    public FileDownloadApplication(WorkListService workListService, FileDownloadService downloadService) {
        this.workListService = workListService;
        this.downloadService = downloadService;
    }

    public static void main(String[] args) {
        SpringApplication.run(FileDownloadApplication.class, args);
    }

    @Scheduled(fixedRate = 10_000)
    public void procressWorkList() {
        logger.info("Scheduled job");
        List<WorkListEntry> entries = workListService.fetchEntries();
        if (entries != null && !entries.isEmpty()) {
            logger.info("Number of records: {}", entries.size());
            entries.parallelStream().forEach(entry -> {
                downloadService.download(entry);
            });
        } else {
            logger.info("No new records");
        }
    }
}
