package com.coachly.adminpanel.common.storage;

import com.coachly.adminpanel.config.StorageProperties;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@ConditionalOnProperty(name = "app.storage.provider", havingValue = "gcs")
public class GcsStorageService implements StorageService {

    private final StorageProperties storageProperties;
    private final Storage gcsClient;
    private final String bucket;

    public GcsStorageService(StorageProperties storageProperties) {
        this.storageProperties = storageProperties;
        StorageProperties.Gcs gcs = storageProperties.gcs();
        this.bucket = gcs != null ? gcs.bucket() : null;
        this.gcsClient = StorageOptions.getDefaultInstance().getService();
    }

    @Override
    public String uploadFile(MultipartFile file, String folder) throws IOException {
        if (!StringUtils.hasText(bucket)) {
            throw new IllegalStateException("GCS bucket name is not configured under app.storage.gcs.bucket");
        }

        String key = generateKey(file, folder);
        BlobInfo blobInfo = BlobInfo.newBuilder(bucket, key)
                .setContentType(file.getContentType())
                .build();
        gcsClient.create(blobInfo, file.getBytes());
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

        return "https://storage.googleapis.com/%s/%s".formatted(bucket, key);
    }
}
