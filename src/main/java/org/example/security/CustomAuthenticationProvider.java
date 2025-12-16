package org.example.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.persistence.entity.UserEntity;
import org.example.persistence.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        log.info("Authenticating user: {}", username);

        UserEntity user = userRepository.findByUserName(username)
                .orElseThrow(() -> {
                    log.error("User not found: {}", username);
                    return new BadCredentialsException("Invalid username or password");
                });

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            log.error("Invalid password for user: {}", username);
            throw new BadCredentialsException("Invalid username or password");
        }

        // getRoles() returns List<Role>, which implements GrantedAuthority
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> (GrantedAuthority) role)
                .collect(Collectors.toList());

        log.info("User authenticated: {} with roles: {}", username,
                authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));

        return new UsernamePasswordAuthenticationToken(username, password, authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}