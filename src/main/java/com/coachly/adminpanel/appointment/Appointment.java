package com.coachly.adminpanel.appointment;

import com.coachly.adminpanel.client.Client;
import com.coachly.adminpanel.common.BaseEntity;
import com.coachly.adminpanel.professional.Professional;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "appointments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "UUID")
    private UUID id;

    @JdbcTypeCode(SqlTypes.ENUM)
    @Column(name = "type", nullable = false, columnDefinition = "appointment_type")
    private AppointmentType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professional_id", nullable = false)
    private Professional professional;

    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @Column(name = "end_time", nullable = false)
    private Instant endTime;

    @JdbcTypeCode(SqlTypes.ENUM)
    @Column(name = "status", columnDefinition = "appointment_status")
    private AppointmentStatus status;

    @Column(name = "description")
    private String description;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "client_appointments",
            joinColumns = @JoinColumn(name = "appointment_id"),
            inverseJoinColumns = @JoinColumn(name = "client_id")
    )
    @Builder.Default
    private Set<Client> clients = new HashSet<>();
}
