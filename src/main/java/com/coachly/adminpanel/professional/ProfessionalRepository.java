package com.coachly.adminpanel.professional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProfessionalRepository extends JpaRepository<Professional, UUID> {
    Optional<Professional> findByUsername(String username);

    @Query("SELECT DISTINCT p FROM Professional p LEFT JOIN FETCH p.appointments LEFT JOIN FETCH p.disciplines WHERE p.username = :username")
    Optional<Professional> findByUsernameWithDetails(@Param("username") String username);

    @Query("SELECT DISTINCT p FROM Professional p LEFT JOIN FETCH p.appointments LEFT JOIN FETCH p.disciplines WHERE p.id = :id")
    Optional<Professional> findByIdWithDetails(@Param("id") UUID id);

    @Query("SELECT DISTINCT p FROM Professional p LEFT JOIN FETCH p.disciplines")
    List<Professional> findAllWithDisciplines();
}
