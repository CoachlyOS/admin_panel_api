package com.coachly.adminpanel.professional.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateProfessionalRequest(
        @NotBlank String username,
        @NotBlank String firstName,
        @NotBlank String lastName
) {
}
