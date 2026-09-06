package com.coachly.adminpanel.professional.dto;

import java.util.Map;

public record ProfessionalProfileResponse(
        String username,
        String firstName,
        String lastName,
        String locale,
        Map<String, String> biography
) {}
