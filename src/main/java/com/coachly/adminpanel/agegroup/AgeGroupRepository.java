package com.coachly.adminpanel.agegroup;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AgeGroupRepository extends JpaRepository<AgeGroup, UUID> {
}
