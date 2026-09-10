package com.coachly.adminpanel.discipline.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.Map;

public record DisciplineRequest(
        @NotBlank String slug,
        @NotEmpty Map<String, String> name
) {}
