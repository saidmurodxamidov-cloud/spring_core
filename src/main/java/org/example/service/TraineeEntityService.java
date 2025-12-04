package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.TraineeEntity;
import org.example.exception.EntityNotFoundException;
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
        String password = new String(PasswordGenerator.generatePassword());

        trainee.setPassword(bcrypt.encode(password).toCharArray());
        trainee.setUserName(UsernameGenerator.generateUsername(trainee.getFirstName(),trainee.getLastName(),userRepository.findAllUserNames()));

        log.info("creating trainee with username {}", trainee.getUserName());

        TraineeEntity traineeEntity = traineeMapper.toEntity(trainee);
        traineeRepository.save(traineeEntity);

        log.info("trainee created successfully with username: {}", traineeEntity.getUser().getUserName());

        return trainee;
    }

    public boolean authenticate(String username,String password){
       return traineeRepository.findByUserUserName(username)
               .map(t -> bcrypt.matches(password,String.valueOf(t.getUser().getPassword())))
               .orElse(false);
    }
    @Transactional(readOnly = true)
    public Trainee getTraineeByUsername(String username) {
        log.debug("Fetching trainee by username: {}", username);

        TraineeEntity trainee = traineeRepository.findByUserUserName(username)
                .orElseThrow(() -> new EntityNotFoundException("Trainee not found: " + username));

        return traineeMapper.toDTO(trainee);
    }

}
