package com.coachly.adminpanel.client.dto;

import java.util.UUID;

public record ClientResponse(
        UUID id,
        String firstName,
        String lastName,
        String locale,
        Boolean isActive
) {}
