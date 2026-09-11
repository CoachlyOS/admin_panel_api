package com.coachly.adminpanel.professional.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.util.Map;
import java.util.Set;

public record UpdateProfessionalRequest(
        @Schema(description = "Username of the professional to update", example = "johndoe")
        @NotBlank String username,

        @Schema(description = "Updated first name", example = "John")
        String firstName,

        @Schema(description = "Updated last name", example = "Doe")
        String lastName,

        @Schema(description = "Professional bio or introduction", example = "Experienced personal trainer.")
        String biography,

        @Schema(description = "Preferred language locale code", example = "en")
        String locale,

        @Schema(description = "Associated disciplines/skills", example = "[\"Yoga\", \"Nutrition\"]")
        Set<String> disciplines,

        @Schema(description = "Social media links", example = "{\"instagram\": \"johndoe_fit\"}")
        Map<String, String> socials
) {
}
