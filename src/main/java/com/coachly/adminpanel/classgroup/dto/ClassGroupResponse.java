package com.coachly.adminpanel.classgroup.dto;

import com.coachly.adminpanel.agegroup.dto.AgeGroupResponse;
import com.coachly.adminpanel.level.dto.LevelResponse;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record ClassGroupResponse(
        UUID id,
        ClassGroupDisciplineResponse discipline,
        LevelResponse level,
        AgeGroupResponse ageGroup,
        ClassGroupTrainerResponse defaultTrainer,
        Integer capacity,
        Map<String, String> name,
        Map<String, String> description,
        Boolean isActive,
        Instant createdAt,
        Instant updatedAt
) {}
