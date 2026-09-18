package com.coachly.adminpanel.classgroup.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Map;
import java.util.UUID;

public record ClassGroupRequest(
        UUID id,
        @NotNull UUID disciplineId,
        @NotNull UUID levelId,
        @NotNull UUID ageGroupId,
        @NotNull UUID defaultTrainerId,
        @NotNull @Min(1) Integer capacity,
        @NotEmpty Map<String, String> name,
        Map<String, String> description
) {}
