package com.coachly.adminpanel.dto;

public record LoginResponse(
        String token,
        UserProfileResponse user
) {}
