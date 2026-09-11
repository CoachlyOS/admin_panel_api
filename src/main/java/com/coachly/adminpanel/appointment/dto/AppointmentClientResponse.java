package com.coachly.adminpanel.appointment.dto;

import java.util.UUID;

public record AppointmentClientResponse(
        UUID id,
        String firstName,
        String lastName
) {}
