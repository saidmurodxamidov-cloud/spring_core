package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.persistence.entity.UserEntity;
import org.example.persistence.repository.UserRepository;
import org.example.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceJpa implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bcrypt;

    public boolean passwordMatches(String username,String password){
        Optional<UserEntity> userOptional = userRepository.findByUserName(username);
        if(userOptional.isEmpty())
            return false;
        UserEntity user = userOptional.get();
        return bcrypt.matches(password,user.getPasswordHash());
    }


    @Transactional
    public boolean changePassword(String userName,String newPassword){
        Optional<UserEntity> userOptional = userRepository.findByUserName(userName);
        if(userOptional.isEmpty())
            return false;
        UserEntity user = userOptional.get();
        user.setPasswordHash(newPassword);
        userRepository.save(user);
        return true;
    }
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public boolean toggleUserActiveStatus(String username) {
        UserEntity user = userRepository.findByUserName(username).orElseThrow(() -> new UsernameNotFoundException(username + ": user does exist"));
        user.setActive(!user.isActive());

        return user.isActive();
    }
}
