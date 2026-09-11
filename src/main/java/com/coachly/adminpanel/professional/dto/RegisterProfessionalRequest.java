package com.coachly.adminpanel.professional.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.util.Set;

public record RegisterProfessionalRequest(
        @Schema(description = "Unique username for the professional account", example = "johndoe")
        @NotBlank String username,

        @Schema(description = "Secure password", example = "SecurePass123!")
        @NotBlank String password,

        @Schema(description = "First name of the professional", example = "John")
        @NotBlank String firstName,

        @Schema(description = "Last name of the professional", example = "Doe")
        @NotBlank String lastName
) {
}
