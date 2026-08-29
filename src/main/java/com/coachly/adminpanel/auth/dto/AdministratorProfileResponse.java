package com.coachly.adminpanel.auth.dto;

import com.coachly.adminpanel.administrator.AdministratorRole;
import java.util.UUID;

public record AdministratorProfileResponse(
        UUID id,
        String username,
        String email,
        AdministratorRole role
) {}
