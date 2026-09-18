package com.coachly.adminpanel.level;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LevelRepository extends JpaRepository<Level, UUID> {
}
