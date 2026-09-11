package com.coachly.adminpanel.professional.dto;

public record ProfessionalResponse(
        String username,
        String firstName,
        String lastName,
        String locale,
        Boolean isActive,
        String avatarUrl,
        Integer subscriptionCount
) {}
