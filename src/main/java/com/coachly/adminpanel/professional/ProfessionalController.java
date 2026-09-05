package com.coachly.adminpanel.professional;

import com.coachly.adminpanel.professional.dto.RegisterProfessionalRequest;
import com.coachly.adminpanel.professional.dto.UpdateProfessionalRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/professional")
@RequiredArgsConstructor
public class ProfessionalController {

    private final ProfessionalService professionalService;

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN') || hasRole('MANAGER')")
    public ResponseEntity<?> addNewProfessional(@RequestBody @Valid RegisterProfessionalRequest professionalRequest) {
        professionalService.registerProfessional(professionalRequest);
        return ResponseEntity.ok().build();
    }

    @PatchMapping
    @PreAuthorize("hasRole('ADMIN') || hasRole('MANAGER')")
    public ResponseEntity<?> updateProfessional(@RequestBody @Valid UpdateProfessionalRequest professionalRequest) {
        professionalService.updateProfessional(professionalRequest);
        return ResponseEntity.ok().build();
    }
}
