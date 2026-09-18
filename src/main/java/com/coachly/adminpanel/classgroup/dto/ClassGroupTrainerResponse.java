package com.coachly.adminpanel.classgroup.dto;

import java.util.Map;
import java.util.UUID;

public record ClassGroupTrainerResponse(
        UUID id,
        String username,
        String firstName,
        String lastName,
        Boolean isActive
) {}
