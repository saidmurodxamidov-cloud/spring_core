package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.TraineeEntity;
import org.example.mapper.TraineeMapper;
import org.example.model.Trainee;
import org.example.repository.TraineeRepository;
import org.example.repository.UserRepository;
import org.example.util.PasswordGenerator;
import org.example.util.UsernameGenerator;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class TraineeEntityService {
    private final TraineeRepository traineeRepository;
    private final TraineeMapper traineeMapper;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bcrypt;


    @Transactional
    public Trainee createTrainee(Trainee trainee){
        Set<String> availableUsernames = userRepository.findAllUserNames();
        String password = new String(PasswordGenerator.generatePassword());
        String username = UsernameGenerator.generateUsername(trainee.getFirstName(),trainee.getLastName(),availableUsernames);
        String encodedPassword = bcrypt.encode(password);
        trainee.setPassword(encodedPassword.toCharArray());
        trainee.setUserName(username);

        log.info("creating trainee with username {}", trainee.getUserName());

        TraineeEntity traineeEntity = traineeMapper.toEntity(trainee);
        traineeRepository.save(traineeEntity);

        log.info("trainee created successfully with username: {}", traineeEntity.getUser().getUserName());

        return trainee;
    }

}
