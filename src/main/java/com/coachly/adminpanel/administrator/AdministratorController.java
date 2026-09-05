package com.coachly.adminpanel.administrator;

import com.coachly.adminpanel.administrator.dto.RegisterManagerRequest;
import com.coachly.adminpanel.common.ErrorResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/administrator")
@AllArgsConstructor
public class AdministratorController {

    private final AdministratorRepository administratorRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register-manager")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> registerManager(@RequestBody RegisterManagerRequest request) {
        var username = request.username();

        if (administratorRepository.findByUsername(username).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse("Username is already in use"));
        }

        var manager = Administrator.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .role(AdministratorRole.MANAGER)
                .build();

        administratorRepository.save(manager);

        return ResponseEntity.ok().build();
    }
}
