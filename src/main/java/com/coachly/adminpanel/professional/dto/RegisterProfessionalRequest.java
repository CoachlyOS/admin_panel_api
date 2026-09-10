package com.coachly.adminpanel.professional.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.Set;

public record RegisterProfessionalRequest(
        @NotBlank String username,
        @NotBlank String password,
        @NotBlank String firstName,
        @NotBlank String lastName
) {
}
