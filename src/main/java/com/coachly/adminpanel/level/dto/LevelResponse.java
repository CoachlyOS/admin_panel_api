package com.coachly.adminpanel.level.dto;

import java.util.Map;
import java.util.UUID;

public record LevelResponse(
        UUID id,
        String slug,
        Map<String, String> name,
        Boolean isActive
) {}
