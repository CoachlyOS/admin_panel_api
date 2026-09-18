package com.coachly.adminpanel.discipline.dto;

import java.util.Map;
import java.util.UUID;

public record DisciplineResponse(
        UUID id,
        String slug,
        Map<String, String> name
) {}
