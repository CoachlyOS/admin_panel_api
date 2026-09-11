package com.coachly.adminpanel.professional;

import com.coachly.adminpanel.professional.dto.ProfessionalSubscriptionCount;
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

    @Query(value = "SELECT COUNT(*) FROM subscriptions s JOIN clients c ON s.client_id = c.id WHERE s.professional_id = :professionalId AND c.is_active = true", nativeQuery = true)
    int countActiveSubscribersByProfessionalId(@Param("professionalId") UUID professionalId);

    @Query(value = "SELECT s.professional_id AS professionalId, COUNT(*) AS activeCount FROM subscriptions s JOIN clients c ON s.client_id = c.id WHERE c.is_active = true GROUP BY s.professional_id", nativeQuery = true)
    List<ProfessionalSubscriptionCount> countActiveSubscribersAll();
}
