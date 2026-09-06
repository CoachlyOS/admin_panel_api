package com.coachly.adminpanel.professional.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateProfessionalRequest(
        @NotBlank String username,
        String firstName,
        String lastName,
        String biography,
        String locale
) {
}
