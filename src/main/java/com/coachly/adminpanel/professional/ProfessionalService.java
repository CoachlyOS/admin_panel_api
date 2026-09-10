package com.coachly.adminpanel.professional;

import com.coachly.adminpanel.common.exception.ResourceNotFoundException;
import com.coachly.adminpanel.common.exception.UsernameAlreadyExistsException;
import com.coachly.adminpanel.common.storage.StorageService;
import com.coachly.adminpanel.discipline.Discipline;
import com.coachly.adminpanel.discipline.DisciplineRepository;
import com.coachly.adminpanel.discipline.dto.DisciplineResponse;
import com.coachly.adminpanel.professional.dto.AvatarResponse;
import com.coachly.adminpanel.professional.dto.ProfessionalProfileResponse;
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
import java.util.Map;
import java.util.Set;
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

    @Transactional(readOnly = true)
    public ProfessionalProfileResponse getProfessional(String username) {
        return professionalRepository.findByUsername(username)
                .map(professional -> new ProfessionalProfileResponse(
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
                        storageService.resolveUrl(professional.getAvatarId())
                ))
                .orElseThrow(() -> new ResourceNotFoundException("Username not found"));
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
            throw new RuntimeException("Failed to upload avatar", e);
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
            Set<Discipline> newDisciplines = request.disciplines().stream()
                    .map(slug -> disciplineRepository.findBySlug(slug)
                            .orElseThrow(() -> new ResourceNotFoundException("Discipline not found with slug: " + slug)))
                    .collect(Collectors.toSet());
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
