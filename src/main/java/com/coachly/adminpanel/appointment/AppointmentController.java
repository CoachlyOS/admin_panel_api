package com.coachly.adminpanel.appointment;

import com.coachly.adminpanel.appointment.dto.AppointmentDetailResponse;
import com.coachly.adminpanel.appointment.dto.AppointmentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/appointment")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN') || hasRole('MANAGER')")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @GetMapping("/{id}")
    public AppointmentDetailResponse getAppointment(@PathVariable UUID id) {
        return appointmentService.getAppointment(id);
    }

    @GetMapping("/all")
    public Page<AppointmentResponse> getAllAppointments(Pageable pageable) {
        return appointmentService.getAllAppointments(pageable);
    }

    @PostMapping("/{id}/cancel")
    public void cancelAppointment(@PathVariable UUID id) {
        appointmentService.cancelAppointment(id);
    }
}
