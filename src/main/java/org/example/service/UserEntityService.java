package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.entity.UserEntity;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserEntityService {

    private final UserRepository userRepository;

    public boolean passwordMatches(String username,String password){
        Optional<UserEntity> userOptional = userRepository.findByUserName(username);
        if(userOptional.isEmpty())
            return false;
        UserEntity user = userOptional.get();
        return Arrays.equals(password.toCharArray(),user.getPassword());
    }
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
}
