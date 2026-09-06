package com.coachly.adminpanel.professional;

import com.coachly.adminpanel.common.exception.ResourceNotFoundException;
import com.coachly.adminpanel.common.exception.UsernameAlreadyExistsException;
import com.coachly.adminpanel.professional.dto.ProfessionalProfileResponse;
import com.coachly.adminpanel.professional.dto.RegisterProfessionalRequest;
import com.coachly.adminpanel.professional.dto.UpdateProfessionalRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProfessionalService {

    private final ProfessionalRepository professionalRepository;
    private final PasswordEncoder passwordEncoder;

    public void registerProfessional(RegisterProfessionalRequest request) throws DataIntegrityViolationException {
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
        return professionalRepository.findByUsername(username)
                .map(professional -> new ProfessionalProfileResponse(
                        professional.getUsername(),
                        professional.getFirstName(),
                        professional.getLastName(),
                        professional.getLocale(),
                        professional.getBiography()
                ))
                .orElseThrow(() -> new ResourceNotFoundException("Username not found"));
    }

    @Transactional
    public void updateProfessional(UpdateProfessionalRequest request) {
        var hasChanged = false;
        var professional = professionalRepository.findByUsername(request.username())
                .orElseThrow(() -> new ResourceNotFoundException("Username not found"));

        if (StringUtils.hasLength(request.firstName())) {
            professional.setFirstName(request.firstName());
            hasChanged = true;
        }

        if (StringUtils.hasLength(request.lastName())) {
            professional.setLastName(request.lastName());
            hasChanged = true;
        }

        if (StringUtils.hasLength(request.biography())) {
            if (StringUtils.hasLength(request.locale())) {
                Map<String, String> biography = professional.getBiography();

                if (biography == null) {
                    biography = new HashMap<>();
                }

                biography.put(request.locale(), request.biography());
                professional.setBiography(biography);
                hasChanged = true;
            } else {
                throw new IllegalArgumentException("Both biography and locale must be provided together.");
            }
        }

        if (hasChanged) {
            professionalRepository.save(professional);
        }
    }
}
