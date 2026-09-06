package com.coachly.adminpanel.auth;

import com.coachly.adminpanel.auth.dto.AdministratorProfileResponse;
import com.coachly.adminpanel.auth.dto.LoginRequest;
import com.coachly.adminpanel.auth.dto.LoginResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

   private final AuthService authService;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody @Valid LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

    @GetMapping("/validate")
    public ResponseEntity<AdministratorProfileResponse> validate(Authentication authentication) {
        return authService.validate(authentication)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }
}
