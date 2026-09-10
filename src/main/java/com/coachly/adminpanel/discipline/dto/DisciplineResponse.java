package com.coachly.adminpanel.discipline.dto;

import java.util.Map;

public record DisciplineResponse(
        String slug,
        Map<String, String> name
) {}
