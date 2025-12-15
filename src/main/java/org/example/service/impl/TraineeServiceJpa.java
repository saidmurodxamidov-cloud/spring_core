package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.persistence.entity.Role;
import org.example.persistence.entity.TraineeEntity;
import org.example.persistence.entity.TrainerEntity;
import org.example.exception.EntityNotFoundException;
import org.example.mapper.TraineeMapper;
import org.example.persistence.model.TraineeDTO;
import org.example.persistence.repository.TraineeRepository;
import org.example.persistence.repository.TrainerRepository;
import org.example.persistence.repository.UserRepository;
import org.example.service.TraineeService;
import org.example.util.PasswordGenerator;
import org.example.util.UsernameGenerator;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class TraineeServiceJpa implements TraineeService {
    private final TraineeRepository traineeRepository;
    private final TraineeMapper traineeMapper;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bcrypt;
    private final TrainerRepository trainerRepository;

    @Transactional
    public TraineeDTO createTrainee(TraineeDTO traineeDTO){
        Set<String> availableUsernames = userRepository.findAllUserNames();
        String password = new String(PasswordGenerator.generatePassword());
        String username = UsernameGenerator.generateUsername(traineeDTO.getFirstName(), traineeDTO.getLastName(),availableUsernames);
        String encodedPassword = bcrypt.encode(password);
        traineeDTO.setUserName(username);
        log.info("creating trainee with username {}", traineeDTO.getUserName());
        TraineeEntity traineeEntity = traineeMapper.toEntity(traineeDTO);
        traineeEntity.getUser().setPasswordHash(encodedPassword);
        traineeEntity.getUser().setRoles(new HashSet<>());
        traineeEntity.getUser().getRoles().add(Role.TRAINEE);
        traineeRepository.save(traineeEntity);

        log.info("trainee created successfully with username: {}", traineeEntity.getUser().getUserName());

        return traineeMapper.toDTO(traineeEntity);
    }
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('TRAINEE') or hasRole('ADMIN')")
    public TraineeDTO getTraineeByUsername(String username){
        log.debug("getting trainee with username: {}",username);
        return traineeRepository.findByUserUserName(username)
                .map(traineeMapper::toDTO)
                .orElseThrow((() -> new UsernameNotFoundException("user not found with username: " + username)));
    }
    @Transactional
    @PreAuthorize("hasRole('TRAINEE') or hasRole('ADMIN')")
    public void updateTrainersList(String traineeUsername, List<String> trainersUsernames) {
        log.debug("updating trainee: {}'s trainers", traineeUsername);
        TraineeEntity trainee = traineeRepository.findByUserUserName(traineeUsername)
                .orElseThrow(() -> new UsernameNotFoundException("trainee: " + traineeUsername + "does not exist"));

        Set<TrainerEntity> newTrainers = trainerRepository.findByUserUserNameIn(trainersUsernames);
        trainee.setTrainers(newTrainers);
        traineeRepository.save(trainee);
        log.info("trainee {}'s trainers updated successfully", traineeUsername);
    }
    @Transactional
    @PreAuthorize("hasRole('TRAINEE') or hasRole('ADMIN')")
    public void deleteByUsername(String username) {
        log.debug("deleting user with username: {}", username);
        TraineeEntity trainee = traineeRepository.findByUserUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("user does not exist: " + username));

        traineeRepository.delete(trainee);
        log.info("user: {} is deleted successfully", username);
    }
    @Transactional
    @PreAuthorize("hasRole('TRAINEE') or hasRole('ADMIN')")
    public TraineeDTO updateTrainee(String username, TraineeDTO updateDTO){
        log.debug("updating trainee with username: {}",username);
        TraineeEntity traineeEntity = traineeRepository.findByUserUserName(username)
                .orElseThrow(() -> new EntityNotFoundException("trainee with username: " + username + "not found"));
        traineeMapper.updateEntity(updateDTO,traineeEntity);
        traineeRepository.save(traineeEntity);
        log.info("updated successfully trainee with username: {}", username);
        return updateDTO;
    }
}


