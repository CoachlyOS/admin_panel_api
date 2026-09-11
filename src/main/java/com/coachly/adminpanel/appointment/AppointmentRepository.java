package com.coachly.adminpanel.appointment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    @Query(value = "SELECT a FROM Appointment a LEFT JOIN FETCH a.professional",
           countQuery = "SELECT COUNT(a) FROM Appointment a")
    Page<Appointment> findAllWithProfessional(Pageable pageable);

    @Query("SELECT a FROM Appointment a LEFT JOIN FETCH a.professional LEFT JOIN FETCH a.clients WHERE a.id = :id")
    Optional<Appointment> findByIdWithDetails(@Param("id") UUID id);
}
