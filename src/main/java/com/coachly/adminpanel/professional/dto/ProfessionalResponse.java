package com.coachly.adminpanel.professional.dto;

import com.coachly.adminpanel.discipline.dto.DisciplineResponse;
import java.util.List;
import java.util.UUID;

public record ProfessionalResponse(
        UUID id,
        String username,
        String firstName,
        String lastName,
        String locale,
        Boolean isActive,
        String avatarUrl,
        List<DisciplineResponse> disciplines
) {}
