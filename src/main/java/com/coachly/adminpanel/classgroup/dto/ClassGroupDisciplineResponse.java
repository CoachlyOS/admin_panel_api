package com.coachly.adminpanel.classgroup.dto;

import java.util.Map;
import java.util.UUID;

public record ClassGroupDisciplineResponse(
        UUID id,
        String slug,
        Map<String, String> name
) {}
