package com.coachly.adminpanel.common.storage;

import com.coachly.adminpanel.config.StorageProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

@Service
@ConditionalOnProperty(name = "app.storage.provider", havingValue = "s3")
public class S3StorageService implements StorageService {

    private final StorageProperties storageProperties;
    private final S3Client s3Client;
    private final String bucket;
    private final String region;

    public S3StorageService(StorageProperties storageProperties) {
        this.storageProperties = storageProperties;
        StorageProperties.S3 s3 = storageProperties.s3();
        String configuredRegion = s3 != null ? s3.region() : null;
        this.region = StringUtils.hasText(configuredRegion) ? configuredRegion : "us-east-1";
        this.bucket = s3 != null ? s3.bucket() : null;
        this.s3Client = S3Client.builder()
                .region(Region.of(this.region))
                .build();
    }

    @Override
    public String uploadFile(MultipartFile file, String folder) throws IOException {
        if (!StringUtils.hasText(bucket)) {
            throw new IllegalStateException("S3 bucket name is not configured under app.storage.s3.bucket");
        }

        String key = generateKey(file, folder);
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(file.getContentType())
                .build();
        s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        return key;
    }

    @Override
    public String resolveUrl(String key) {
        String cdnUrl = resolveCdnUrl(storageProperties.cdnBaseUrl(), key);
        if (cdnUrl != null) {
            return cdnUrl;
        }

        if (!StringUtils.hasText(key) || !StringUtils.hasText(bucket)) {
            return null;
        }

        return "https://%s.s3.%s.amazonaws.com/%s".formatted(bucket, region, key);
    }
}
