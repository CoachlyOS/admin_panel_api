package com.coachly.adminpanel.auth;

import com.coachly.adminpanel.auth.dto.LoginRequest;
import com.coachly.adminpanel.auth.dto.LoginResponse;
import com.coachly.adminpanel.auth.dto.UserProfileResponse;
import com.coachly.adminpanel.common.ErrorResponse;
import com.coachly.adminpanel.security.JwtTokenProvider;
import com.coachly.adminpanel.user.UserRepository;
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
    private final UserRepository userRepository;

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

            var user = userRepository.findByUsername(username).orElseThrow();
            var userProfile = new UserProfileResponse(user.getId(), user.getUsername(), user.getEmail(), roles);

            return ResponseEntity.ok(new LoginResponse(token, userProfile));
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
        var roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return userRepository.findByUsername(username)
                .map(user -> ResponseEntity.ok(
                        new UserProfileResponse(user.getId(), user.getUsername(), user.getEmail(), roles)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }
}
