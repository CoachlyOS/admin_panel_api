package com.coachly.adminpanel.agegroup.dto;

import java.util.Map;
import java.util.UUID;

public record AgeGroupResponse(
        UUID id,
        String slug,
        Map<String, String> name,
        Boolean isActive
) {}
