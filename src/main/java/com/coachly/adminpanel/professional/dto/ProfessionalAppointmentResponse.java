package com.coachly.adminpanel.professional.dto;

import com.coachly.adminpanel.appointment.AppointmentStatus;
import com.coachly.adminpanel.appointment.AppointmentType;

import java.time.Instant;
import java.util.UUID;

public record ProfessionalAppointmentResponse(
        UUID id,
        AppointmentType type,
        Instant startTime,
        Instant endTime,
        AppointmentStatus status,
        String description
) {}
