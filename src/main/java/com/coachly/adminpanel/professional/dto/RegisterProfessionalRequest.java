package com.coachly.adminpanel.professional.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.util.Map;
import java.util.Set;

public record RegisterProfessionalRequest(
        @Schema(description = "Unique username for the professional account", example = "johndoe")
        @NotBlank String username,

        @Schema(description = "Secure password", example = "SecurePass123!")
        @NotBlank String password,

        @Schema(description = "First name of the professional", example = "John")
        @NotBlank String firstName,

        @Schema(description = "Last name of the professional", example = "Doe")
        @NotBlank String lastName,

        @Schema(description = "Preferred/display locale code for the professional (defaults to 'en' when omitted)", example = "en")
        String locale,

        @Schema(description = "Full locale→text biography map", example = "{\"en\":\"Experienced trainer.\"}")
        Map<String, String> biography,

        @Schema(description = "Associated discipline slugs to assign on creation", example = "[\"yoga\",\"nutrition\"]")
        Set<String> disciplines,

        @Schema(description = "Social media links", example = "{\"instagram\": \"johndoe_fit\"}")
        Map<String, String> socials
) {
}
