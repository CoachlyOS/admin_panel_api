package com.coachly.adminpanel.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterManagerRequest(
        @NotBlank String username,
        @NotBlank String password
) {
}
