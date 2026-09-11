package com.coachly.adminpanel.appointment.dto;

import com.coachly.adminpanel.appointment.AppointmentStatus;
import com.coachly.adminpanel.appointment.AppointmentType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record AppointmentDetailResponse(
        UUID id,
        AppointmentType type,
        AppointmentProfessionalResponse professional,
        Instant startTime,
        Instant endTime,
        AppointmentStatus status,
        String description,
        List<AppointmentClientResponse> clients
) {}
