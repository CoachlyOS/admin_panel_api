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

        @Schema(description = "Preferred/display locale code for the professional (e.g. for Telegram bot UI), independent of biography map keys", example = "en")
        String locale,

        @Schema(description = "Full locale→text biography map; when present, fully replaces the existing biography (not merged key-by-key)", example = "{\"en\":\"Experienced trainer.\",\"uk\":\"Досвідчений тренер.\"}")
        Map<String, String> biography,

        @Schema(description = "Associated discipline slugs; when present, fully replaces the professional's disciplines", example = "[\"yoga\",\"nutrition\"]")
        Set<String> disciplines,

        @Schema(description = "Social media links; when present, fully replaces the existing socials", example = "{\"instagram\": \"johndoe_fit\"}")
        Map<String, String> socials,

        @Schema(description = "Active flag; when present (non-null) sets the professional's isActive state in either direction (activate/deactivate). When null/absent, the current value is left untouched.", example = "true")
        Boolean isActive
) {
}
