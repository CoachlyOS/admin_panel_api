package com.coachly.adminpanel.professional.dto;

import com.coachly.adminpanel.discipline.dto.DisciplineResponse;
import java.util.List;
import java.util.Map;

public record ProfessionalProfileResponse(
        String username,
        String firstName,
        String lastName,
        String locale,
        Map<String, String> biography,
        List<DisciplineResponse> disciplines
) {}
