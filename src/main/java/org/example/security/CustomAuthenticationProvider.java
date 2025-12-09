package org.example.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.UserEntity;
import org.example.repository.TraineeRepository;
import org.example.repository.TrainerRepository;
import org.example.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        log.info("Authenticating user: {}", username);

        UserEntity user = userRepository.findByUserName(username)
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        if (!passwordEncoder.matches(password, String.valueOf(user.getPassword()))) {
            log.error("Invalid password for user: {}", username);
            throw new BadCredentialsException("Invalid username or password");
        }

        if (!user.isActive()) {
            log.error("UserEntity account is inactive: {}", username);
            throw new BadCredentialsException("UserEntity account is inactive");
        }

        List<GrantedAuthority> authorities = getAuthorities(user);

        log.info("UserEntity authenticated successfully: {}", username);
        return new UsernamePasswordAuthenticationToken(username, password, authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    private List<GrantedAuthority> getAuthorities(UserEntity user) {
        return user.getRoles().stream()
                .map(role -> (GrantedAuthority) role)
                .collect(Collectors.toList());
    }

}