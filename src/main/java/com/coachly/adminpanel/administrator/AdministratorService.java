package com.coachly.adminpanel.administrator;

import com.coachly.adminpanel.administrator.dto.RegisterManagerRequest;
import com.coachly.adminpanel.common.exception.UsernameAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdministratorService {

    private final AdministratorRepository administratorRepository;
    private final PasswordEncoder passwordEncoder;

    public void registerManager(RegisterManagerRequest request) throws DataIntegrityViolationException {
        if (administratorRepository.findByUsername(request.username()).isPresent()) {
            throw new UsernameAlreadyExistsException("Username is already in use");
        }

        var manager = Administrator.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .role(AdministratorRole.MANAGER)
                .build();

        administratorRepository.save(manager);
    }
}
