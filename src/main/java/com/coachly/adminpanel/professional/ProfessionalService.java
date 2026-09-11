package com.coachly.adminpanel.professional;

import com.coachly.adminpanel.common.exception.ResourceNotFoundException;
import com.coachly.adminpanel.common.exception.StorageException;
import com.coachly.adminpanel.common.exception.UsernameAlreadyExistsException;
import com.coachly.adminpanel.common.storage.StorageService;
import com.coachly.adminpanel.discipline.Discipline;
import com.coachly.adminpanel.discipline.DisciplineRepository;
import com.coachly.adminpanel.discipline.dto.DisciplineResponse;
import com.coachly.adminpanel.professional.dto.AvatarResponse;
import com.coachly.adminpanel.professional.dto.ProfessionalAppointmentResponse;
import com.coachly.adminpanel.professional.dto.ProfessionalProfileResponse;
import com.coachly.adminpanel.professional.dto.ProfessionalResponse;
import com.coachly.adminpanel.professional.dto.ProfessionalSubscriptionCount;
import com.coachly.adminpanel.professional.dto.RegisterProfessionalRequest;
import com.coachly.adminpanel.professional.dto.UpdateProfessionalRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfessionalService {

    private final ProfessionalRepository professionalRepository;
    private final PasswordEncoder passwordEncoder;
    private final DisciplineRepository disciplineRepository;
    private final StorageService storageService;

    public void registerProfessional(RegisterProfessionalRequest request) {
        if (professionalRepository.findByUsername(request.username()).isPresent()) {
            throw new UsernameAlreadyExistsException("Username is already in use");
        }

        var professional = Professional.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .firstName(request.firstName())
                .lastName(request.lastName())
                .build();

        professionalRepository.save(professional);
    }

    public ProfessionalProfileResponse getProfessional(String username) {
        var professional = professionalRepository.findByUsernameWithDetails(username)
                .orElseThrow(() -> new ResourceNotFoundException("Username not found"));

        int subscriberCount = professionalRepository.countActiveSubscribersByProfessionalId(professional.getId());

        var appointments = professional.getAppointments().stream()
                .map(app -> new ProfessionalAppointmentResponse(
                        app.getId(),
                        app.getType(),
                        app.getStartTime(),
                        app.getEndTime(),
                        app.getStatus(),
                        app.getDescription()
                ))
                .toList();

        return new ProfessionalProfileResponse(
                professional.getUsername(),
                professional.getFirstName(),
                professional.getLastName(),
                professional.getLocale(),
                professional.getIsActive(),
                professional.getBiography(),
                professional.getDisciplines().stream()
                        .map(d -> new DisciplineResponse(d.getSlug(), d.getName()))
                        .toList(),
                professional.getSocials(),
                storageService.resolveUrl(professional.getAvatarId()),
                subscriberCount,
                appointments
        );
    }

    @Transactional(readOnly = true)
    public List<ProfessionalResponse> getAllProfessionals() {
        Map<UUID, Long> subscriberCounts = professionalRepository.countActiveSubscribersAll().stream()
                .collect(Collectors.toMap(
                        ProfessionalSubscriptionCount::getProfessionalId,
                        ProfessionalSubscriptionCount::getActiveCount
                ));

        return professionalRepository.findAll().stream()
                .map(professional -> new ProfessionalResponse(
                        professional.getUsername(),
                        professional.getFirstName(),
                        professional.getLastName(),
                        professional.getLocale(),
                        professional.getIsActive(),
                        storageService.resolveUrl(professional.getAvatarId()),
                        subscriberCounts.getOrDefault(professional.getId(), 0L).intValue()
                ))
                .toList();
    }

    @Transactional
    public AvatarResponse uploadAvatar(String username, MultipartFile file) {
        var professional = professionalRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Username not found"));

        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cannot upload empty file.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed.");
        }

        try {
            String key = storageService.uploadFile(file, "avatars");
            professional.setAvatarId(key);
            professionalRepository.save(professional);

            return new AvatarResponse(storageService.resolveUrl(key));
        } catch (IOException e) {
            throw new StorageException("Failed to upload avatar", e);
        }
    }

    @Transactional
    public void updateProfessional(UpdateProfessionalRequest request) {
        var professional = professionalRepository.findByUsername(request.username())
                .orElseThrow(() -> new ResourceNotFoundException("Username not found"));

        if (StringUtils.hasText(request.firstName())) {
            professional.setFirstName(request.firstName());
        }

        if (StringUtils.hasText(request.lastName())) {
            professional.setLastName(request.lastName());
        }

        if (StringUtils.hasText(request.biography())) {
            if (!StringUtils.hasText(request.locale())) {
                throw new IllegalArgumentException("Both biography and locale must be provided together.");
            }
            Map<String, String> biography = new HashMap<>(professional.getBiography() != null ? professional.getBiography() : Map.of());
            biography.put(request.locale(), request.biography());
            professional.setBiography(biography);
        }

        if (request.disciplines() != null) {
            Set<Discipline> newDisciplines = disciplineRepository.findAllBySlugIn(request.disciplines());
            if (newDisciplines.size() != request.disciplines().size()) {
                Set<String> foundSlugs = newDisciplines.stream().map(Discipline::getSlug).collect(Collectors.toSet());
                String missingSlug = request.disciplines().stream()
                        .filter(slug -> !foundSlugs.contains(slug))
                        .findFirst().orElseThrow();
                throw new ResourceNotFoundException("Discipline not found with slug: " + missingSlug);
            }
            professional.setDisciplines(newDisciplines);
        }

        if (request.socials() != null) {
            professional.setSocials(request.socials());
        }

        professionalRepository.save(professional);
    }

    @Transactional
    public void deactivateProfessional(String username) {
        var professional = professionalRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Username not found"));

        professional.setIsActive(false);
        professionalRepository.save(professional);
    }
}
