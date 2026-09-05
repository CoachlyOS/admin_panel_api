package com.coachly.adminpanel.professional;

import com.coachly.adminpanel.common.ErrorResponse;
import com.coachly.adminpanel.professional.dto.RegisterProfessionalRequest;
import com.coachly.adminpanel.professional.dto.UpdateProfessionalRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/professional")
@AllArgsConstructor
public class ProfessionalController {

    private final ProfessionalRepository professionalRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN') || hasRole('MANAGER')")
    public ResponseEntity<?> addNewProfessional(@RequestBody @Valid RegisterProfessionalRequest professionalRequest) {
        var username = professionalRequest.username();

        if (professionalRepository.findByUsername(username).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse("Username is already in use"));
        }

        var professional = Professional.builder()
                .username(professionalRequest.username())
                .password(passwordEncoder.encode(professionalRequest.password()))
                .firstName(professionalRequest.firstName())
                .lastName(professionalRequest.lastName())
                .build();

        professionalRepository.save(professional);

        return ResponseEntity.ok().build();
    }

    @PatchMapping
    @PreAuthorize("hasRole('ADMIN') || hasRole('MANAGER')")
    public ResponseEntity<?>  updateProfessional(@RequestBody @Valid UpdateProfessionalRequest professionalRequest) {
        var username = professionalRequest.username();

        var optionalProfessional = professionalRepository.findByUsername(username);

        if (optionalProfessional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Username not found"));
        }

        var professional = optionalProfessional.get();

        professional.setFirstName(professionalRequest.firstName());
        professional.setLastName(professionalRequest.lastName());

        professionalRepository.save(professional);

        return ResponseEntity.ok().build();
    }
}
