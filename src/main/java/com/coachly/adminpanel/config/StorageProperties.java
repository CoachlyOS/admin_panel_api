package com.coachly.adminpanel.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

@ConfigurationProperties(prefix = "app.storage")
public record StorageProperties(
        String provider,
        String cdnBaseUrl,
        String localDir,
        S3 s3,
        Gcs gcs
) {
    public StorageProperties {
        if (!StringUtils.hasText(localDir)) {
            localDir = "uploads";
        }
    }

    public record S3(
            String bucket,
            String region
    ) {}

    public record Gcs(
            String bucket
    ) {}
}
