package com.coachly.adminpanel.classgroup;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ClassGroupRepository extends JpaRepository<ClassGroup, UUID> {
    List<ClassGroup> findAllByDefaultTrainerId(UUID trainerId);
}
