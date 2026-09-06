package com.coachly.adminpanel.auth;

import com.coachly.adminpanel.administrator.AdministratorRepository;
import com.coachly.adminpanel.auth.dto.AdministratorProfileResponse;
import com.coachly.adminpanel.auth.dto.LoginRequest;
import com.coachly.adminpanel.auth.dto.LoginResponse;
import com.coachly.adminpanel.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final AdministratorRepository administratorRepository;

    public LoginResponse login(LoginRequest loginRequest) throws AuthenticationException {
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
        // TODO: ADD EMAIL
        var administratorProfile = new AdministratorProfileResponse(
                administrator.getId(),
                administrator.getUsername(),
                administrator.getEmail(),
                administrator.getRole()
        );

        return new LoginResponse(token, administratorProfile);
    }

    public Optional<AdministratorProfileResponse> validate(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        var username = authentication.getName();
        return administratorRepository.findByUsername(username)
                .map(administrator -> new AdministratorProfileResponse(
                        administrator.getId(),
                        administrator.getUsername(),
                        administrator.getEmail(),
                        administrator.getRole()
                ));
    }
}
