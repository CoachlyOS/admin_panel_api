package com.coachly.adminpanel.common.storage;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

public interface StorageService {

    String uploadFile(MultipartFile file, String folder) throws IOException;

    String resolveUrl(String key);

    default String generateKey(MultipartFile file, String folder) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cannot upload empty file.");
        }

        String extension = Optional.ofNullable(file.getOriginalFilename())
                .filter(name -> name.contains("."))
                .map(name -> name.substring(name.lastIndexOf(".")))
                .orElse("");

        String uniqueName = UUID.randomUUID() + extension;
        return (StringUtils.hasText(folder) ? folder + "/" : "") + uniqueName;
    }

    default String resolveCdnUrl(String baseUrl, String key) {
        if (!StringUtils.hasText(key)) {
            return null;
        }

        if (StringUtils.hasText(baseUrl)) {
            String cleanBase = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
            String cleanKey = key.startsWith("/") ? key.substring(1) : key;
            return cleanBase + "/" + cleanKey;
        }

        return null;
    }
}
