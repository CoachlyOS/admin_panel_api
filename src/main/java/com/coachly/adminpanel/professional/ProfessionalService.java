package com.coachly.adminpanel.professional;

import com.coachly.adminpanel.common.exception.ResourceNotFoundException;
import com.coachly.adminpanel.common.exception.UsernameAlreadyExistsException;
import com.coachly.adminpanel.professional.dto.RegisterProfessionalRequest;
import com.coachly.adminpanel.professional.dto.UpdateProfessionalRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfessionalService {

    private final ProfessionalRepository professionalRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
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

    @Transactional
    public void updateProfessional(UpdateProfessionalRequest request) {
        var professional = professionalRepository.findByUsername(request.username())
                .orElseThrow(() -> new ResourceNotFoundException("Username not found"));

        professional.setFirstName(request.firstName());
        professional.setLastName(request.lastName());

        professionalRepository.save(professional);
    }
}
