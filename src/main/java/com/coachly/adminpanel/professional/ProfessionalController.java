package com.coachly.adminpanel.professional;

import com.coachly.adminpanel.professional.dto.ProfessionalProfileResponse;
import com.coachly.adminpanel.professional.dto.RegisterProfessionalRequest;
import com.coachly.adminpanel.professional.dto.UpdateProfessionalRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/professional")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN') || hasRole('MANAGER')")
public class ProfessionalController {

    private final ProfessionalService professionalService;

    @PostMapping("/register")
    public void addNewProfessional(@RequestBody @Valid RegisterProfessionalRequest professionalRequest) {
        professionalService.registerProfessional(professionalRequest);
    }

    @PatchMapping("/update")
    public void updateProfessional(@RequestBody @Valid UpdateProfessionalRequest professionalRequest) {
        professionalService.updateProfessional(professionalRequest);
    }

    @GetMapping("/{username}")
    public ProfessionalProfileResponse getProfessional(@PathVariable String username) {
        return professionalService.getProfessional(username);
    }
}
