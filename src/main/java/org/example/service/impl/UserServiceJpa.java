package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.response.AuthResponse;
import org.example.persistence.entity.UserEntity;
import org.example.persistence.repository.UserRepository;
import org.example.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceJpa implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bcrypt;

    @Transactional
    @PreAuthorize("hasRole('TRAINEE') or hasRole('ADMIN') or hasRole('TRAINER')")
    public AuthResponse changePassword(String usernameFromToken, String oldPassword, String newPassword) {
        log.debug("Changing password for user: {}", usernameFromToken);

        UserEntity user = userRepository.findByUserName(usernameFromToken)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!bcrypt.matches(oldPassword, user.getPasswordHash())) {
            throw new BadCredentialsException("Old password is incorrect");
        }

        user.setPasswordHash(bcrypt.encode(newPassword));
        log.info("Password changed successfully for user {}", usernameFromToken);
        return new AuthResponse(usernameFromToken,newPassword);
    }

    @Transactional
    @PreAuthorize("hasRole('TRAINEE') or hasRole('TRAINER')")
    public Boolean toggleUserActiveStatus(String username) {
        UserEntity user = userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException(username + ": user does exist"));
        user.setActive(!user.isActive());
        return user.isActive();
    }
}

