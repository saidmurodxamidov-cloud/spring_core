package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.TraineeEntity;
import org.example.exception.EntityNotFoundException;
import org.example.mapper.TraineeMapper;
import org.example.model.TraineeDTO;
import org.example.repository.TraineeRepository;
import org.example.repository.UserRepository;
import org.example.util.PasswordGenerator;
import org.example.util.UsernameGenerator;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public TraineeDTO createTrainee(TraineeDTO traineeDTO){
        Set<String> availableUsernames = userRepository.findAllUserNames();
        String password = new String(PasswordGenerator.generatePassword());
        String username = UsernameGenerator.generateUsername(traineeDTO.getFirstName(), traineeDTO.getLastName(),availableUsernames);
        String encodedPassword = bcrypt.encode(password);
        traineeDTO.setPassword(encodedPassword.toCharArray());
        traineeDTO.setUserName(username);

        log.info("creating trainee with username {}", traineeDTO.getUserName());

        TraineeEntity traineeEntity = traineeMapper.toEntity(traineeDTO);
        traineeRepository.save(traineeEntity);

        log.info("trainee created successfully with username: {}", traineeEntity.getUser().getUserName());

        return traineeDTO;
    }
        public boolean authenticate(String username,String password){

            TraineeEntity trainee = traineeRepository.findByUserUserName(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
            if(!trainee.getUser().isActive())
                throw new IllegalStateException("user is not active: " + username);

            boolean matches = bcrypt.matches(password,String.valueOf(trainee.getUser().getPassword()));
            if(!matches)
                throw new BadCredentialsException("wrong passwrod for user: " + username);

            return true;
        }
    @Transactional(readOnly = true)
    public TraineeDTO getTraineeByUsername(String username){
        log.debug("getting trainee with username: {}",username);
        return traineeRepository.findByUserUserName(username)
                .map(traineeMapper::toDTO)
                .orElseThrow((() -> new UsernameNotFoundException("user not found with username: " + username)));
    }

}


