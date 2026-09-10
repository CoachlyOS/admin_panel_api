package com.coachly.adminpanel.common.storage;

import com.coachly.adminpanel.config.StorageProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.storage.provider", havingValue = "local", matchIfMissing = true)
public class LocalStorageService implements StorageService {

    private final StorageProperties storageProperties;

    @Override
    public String uploadFile(MultipartFile file, String folder) throws IOException {
        String key = generateKey(file, folder);
        Path targetPath = Paths.get(storageProperties.localDir()).resolve(key).normalize();
        Files.createDirectories(targetPath.getParent());
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        return key;
    }

    @Override
    public String resolveUrl(String key) {
        String cdnUrl = resolveCdnUrl(storageProperties.cdnBaseUrl(), key);
        if (cdnUrl != null) {
            return cdnUrl;
        }

        if (!StringUtils.hasText(key)) {
            return null;
        }

        return "/uploads/" + (key.startsWith("/") ? key.substring(1) : key);
    }
}
