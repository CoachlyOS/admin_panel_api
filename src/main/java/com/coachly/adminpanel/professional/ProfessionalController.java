package com.coachly.adminpanel.professional;

import com.coachly.adminpanel.professional.dto.AvatarResponse;
import com.coachly.adminpanel.professional.dto.ProfessionalProfileResponse;
import com.coachly.adminpanel.professional.dto.ProfessionalResponse;
import com.coachly.adminpanel.professional.dto.RegisterProfessionalRequest;
import com.coachly.adminpanel.professional.dto.UpdateProfessionalRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/professional")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN') || hasRole('MANAGER')")
@Tag(name = "Professionals", description = "Endpoints for managing practitioners and coaches")
public class ProfessionalController {

    private final ProfessionalService professionalService;

    @PostMapping("/register")
    @Operation(summary = "Register a new professional")
    public void addNewProfessional(@RequestBody @Valid RegisterProfessionalRequest professionalRequest) {
        professionalService.registerProfessional(professionalRequest);
    }

    @PatchMapping("/update")
    @Operation(summary = "Update professional details")
    public void updateProfessional(@RequestBody @Valid UpdateProfessionalRequest professionalRequest) {
        professionalService.updateProfessional(professionalRequest);
    }

    @GetMapping("/{username}")
    @Operation(summary = "Get professional profile by username")
    public ProfessionalProfileResponse getProfessional(@PathVariable String username) {
        return professionalService.getProfessional(username);
    }

    @GetMapping("/all")
    @Operation(summary = "Get list of all professionals")
    public List<ProfessionalResponse> getAllProfessionals() {
        return professionalService.getAllProfessionals();
    }

    @PostMapping("/{username}/deactivate")
    @Operation(summary = "Deactivate a professional by username")
    public void deactivateProfessional(@PathVariable String username) {
        professionalService.deactivateProfessional(username);
    }

    @PostMapping(value = "/{username}/avatar", consumes = "multipart/form-data")
    @Operation(summary = "Upload professional avatar image")
    public AvatarResponse uploadAvatar(@PathVariable String username, @RequestParam("file") MultipartFile file) {
        return professionalService.uploadAvatar(username, file);
    }
}
