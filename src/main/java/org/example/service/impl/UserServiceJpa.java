package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.UserEntity;
import org.example.repository.UserRepository;
import org.example.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
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
//        if(!bcrypt.matches()){
//            return false;
//        }
        user.setPasswordHash(newPassword);
        userRepository.save(user);
        return true;
    }

    public boolean toggleUserActiveStatus(String username) {
        // confirm authentication for provided username
        UserEntity user = userRepository.findByUserName(username).orElseThrow();
        user.setActive(!user.isActive());

        // maybe smth else

        return user.isActive();
    }
}
