package com.coachly.adminpanel.administrator;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdministratorRepository extends JpaRepository<Administrator, UUID> {
    Optional<Administrator> findByUsername(String username);

    Optional<Administrator> findByEmail(String email);
}
