package com.coachly.adminpanel.administrator;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdministratorDetailsServiceImpl implements UserDetailsService {

    private static final String ROLE_PREFIX = "ROLE_";

    private final AdministratorRepository administratorRepository;

    @Override
    public @NonNull UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        var administrator = administratorRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Administrator not found: " + username));
        var authorities =  AuthorityUtils.createAuthorityList(ROLE_PREFIX + administrator.getRole().name().toUpperCase());

        return new User(administrator.getUsername(), administrator.getPassword(), authorities);
    }
}
