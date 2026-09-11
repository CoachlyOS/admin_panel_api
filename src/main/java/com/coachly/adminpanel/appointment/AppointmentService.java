package com.coachly.adminpanel.appointment;

import com.coachly.adminpanel.appointment.dto.AppointmentClientResponse;
import com.coachly.adminpanel.appointment.dto.AppointmentDetailResponse;
import com.coachly.adminpanel.appointment.dto.AppointmentProfessionalResponse;
import com.coachly.adminpanel.appointment.dto.AppointmentResponse;
import com.coachly.adminpanel.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    public AppointmentDetailResponse getAppointment(UUID id) {
        return appointmentRepository.findByIdWithDetails(id)
                .map(this::mapToDetailResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));
    }

    public Page<AppointmentResponse> getAllAppointments(Pageable pageable) {
        return appointmentRepository.findAllWithProfessional(pageable)
                .map(this::mapToResponse);
    }

    @Transactional
    public void cancelAppointment(UUID id) {
        var appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));

        if (appointment.getStatus() == AppointmentStatus.cancelled) {
            return;
        }
        appointment.setStatus(AppointmentStatus.cancelled);
        appointmentRepository.save(appointment);
    }

    private AppointmentResponse mapToResponse(Appointment appointment) {
        var professional = appointment.getProfessional();
        var professionalResponse = professional != null ? new AppointmentProfessionalResponse(
                professional.getUsername(),
                professional.getFirstName(),
                professional.getLastName()
        ) : null;

        return new AppointmentResponse(
                appointment.getId(),
                appointment.getType(),
                professionalResponse,
                appointment.getStartTime(),
                appointment.getEndTime(),
                appointment.getStatus(),
                appointment.getDescription()
        );
    }

    private AppointmentDetailResponse mapToDetailResponse(Appointment appointment) {
        var professional = appointment.getProfessional();
        var professionalResponse = professional != null ? new AppointmentProfessionalResponse(
                professional.getUsername(),
                professional.getFirstName(),
                professional.getLastName()
        ) : null;

        var clients = appointment.getClients().stream()
                .map(client -> new AppointmentClientResponse(
                        client.getId(),
                        client.getFirstName(),
                        client.getLastName()
                ))
                .toList();

        return new AppointmentDetailResponse(
                appointment.getId(),
                appointment.getType(),
                professionalResponse,
                appointment.getStartTime(),
                appointment.getEndTime(),
                appointment.getStatus(),
                appointment.getDescription(),
                clients
        );
    }
}
