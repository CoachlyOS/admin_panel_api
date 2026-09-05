package com.coachly.adminpanel.administrator.dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterManagerRequest(
        @NotBlank String username,
        @NotBlank String password
) {
}
