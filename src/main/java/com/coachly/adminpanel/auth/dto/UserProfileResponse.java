package com.coachly.adminpanel.auth.dto;

import java.util.Collection;

public record UserProfileResponse(
        Long id,
        String username,
        String email,
        Collection<String> roles
) {}
