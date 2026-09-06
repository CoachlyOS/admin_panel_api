package com.coachly.adminpanel.administrator;

import com.coachly.adminpanel.administrator.dto.RegisterManagerRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/administrator")
@RequiredArgsConstructor
public class AdministratorController {

    private final AdministratorService administratorService;

    @PostMapping("/register-manager")
    @PreAuthorize("hasRole('ADMIN')")
    public void registerManager(@RequestBody @Valid RegisterManagerRequest request) {
        administratorService.registerManager(request);
    }
}
