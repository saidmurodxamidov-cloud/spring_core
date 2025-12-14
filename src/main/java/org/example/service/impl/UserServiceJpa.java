package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.UserEntity;
import org.example.repository.UserRepository;
import org.example.service.UserService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceJpa implements UserService {

    private final UserRepository userRepository;


    public boolean passwordMatches(String username,String password){
        Optional<UserEntity> userOptional = userRepository.findByUserName(username);
        if(userOptional.isEmpty())
            return false;
        UserEntity user = userOptional.get();
        return Arrays.equals(password.toCharArray(),user.getPassword());
    }
    @Transactional
    public boolean changePassword(String userName,String oldPassword,String newPassword){
        Optional<UserEntity> userOptional = userRepository.findByUserName(userName);
        if(userOptional.isEmpty())
            return false;
        UserEntity user = userOptional.get();
        if(!Arrays.equals(user.getPassword(),oldPassword.toCharArray())){
            return false;
        }
        user.setPassword(newPassword.toCharArray());
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
