package com.coachly.adminpanel.auth;

import com.coachly.adminpanel.administrator.AdministratorRepository;
import com.coachly.adminpanel.auth.dto.AdministratorProfileResponse;
import com.coachly.adminpanel.auth.dto.LoginRequest;
import com.coachly.adminpanel.auth.dto.LoginResponse;
import com.coachly.adminpanel.common.ErrorResponse;
import com.coachly.adminpanel.security.JwtTokenProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final AdministratorRepository administratorRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest loginRequest) {
        try {
            var authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.username(),
                            loginRequest.password()
                    )
            );

            var username = authentication.getName();
            var roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            var token = tokenProvider.generateToken(username, roles);

            var administrator = administratorRepository.findByUsername(username).orElseThrow();

            // TODO: Refactor to return only necessary fields
            var administratorProfile = new AdministratorProfileResponse(
                    administrator.getId(),
                    administrator.getUsername(),
                    administrator.getEmail(),
                    administrator.getRole()
            );

            return ResponseEntity.ok(new LoginResponse(token, administratorProfile));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Invalid username or password."));
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validate(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        var username = authentication.getName();

        return administratorRepository.findByUsername(username)
                .map(administrator -> ResponseEntity.ok(
                        new AdministratorProfileResponse(
                                administrator.getId(),
                                administrator.getUsername(),
                                administrator.getEmail(),
                                administrator.getRole()
                        )
                ))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }
}
