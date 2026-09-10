package com.coachly.adminpanel.discipline;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DisciplineRepository extends JpaRepository<Discipline, UUID> {
    Optional<Discipline> findBySlug(String slug);
}
