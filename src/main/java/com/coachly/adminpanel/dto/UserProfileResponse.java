package com.coachly.adminpanel.dto;

import java.util.Collection;

public record UserProfileResponse(
        Long id,
        String username,
        String email,
        Collection<String> roles
) {}
