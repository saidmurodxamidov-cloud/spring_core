package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.TrainerEntity;
import org.example.mapper.TrainerMapper;
import org.example.model.TrainerDTO;
import org.example.repository.TrainerRepository;
import org.example.repository.TrainingTypeRepository;
import org.example.repository.UserRepository;
import org.example.service.TrainingService;
import org.example.util.UsernameGenerator;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainerServiceJpa {

    private final TrainerRepository trainerRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bcrypt;
    private final TrainingService trainingService;
    private final PasswordEncoder passwordEncoder;
    private final TrainerMapper trainerMapper;

    @Transactional
    public TrainerDTO createTrainer(TrainerDTO trainerDTO){
        Set<String> availableUsernames = userRepository.findAllUserNames();
        String password = bcrypt.encode(String.valueOf(trainerDTO.getPassword()));
        String username = UsernameGenerator.generateUsername(trainerDTO.getFirstName(),trainerDTO.getLastName(),availableUsernames);
        log.info("creating trainer with username {}", username);
        trainerDTO.setPassword(password.toCharArray());
        trainerDTO.setUserName(username);

        TrainerEntity trainer = trainerMapper.toEntity(trainerDTO);
        trainerRepository.save(trainer);
        log.info("trainer {} created successfully", username);
        return trainerDTO;
    }

    public TrainerDTO getTrainerByUsername(String username){
        log.debug("getting trainer: {}" , username);
        TrainerEntity trainer = trainerRepository.findByUserUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("trainer " + username + " does not exist"));
        return trainerMapper.toDTO(trainer);
    }
    @Transactional
    public TrainerDTO updateTrainer(TrainerDTO trainerDto){
        log.debug("updating trainer: {}", trainerDto.getUserName());
        TrainerEntity trainerEntity = trainerRepository.findByUserUserName(trainerDto.getUserName())
                .orElseThrow(() -> new UsernameNotFoundException("trainer: " + trainerDto.getUserName() + " does not exist"));
        trainerMapper.updateFromDTO(trainerDto,trainerEntity);
        trainerRepository.save(trainerEntity);
        log.info("trainer {} updated successfully", trainerDto.getUserName());
        return trainerDto;
    }
    @Transactional
    public void setActiveStatus(String username,boolean active){
        log.debug("setting active to {} status to trainer {}", active, username);
        TrainerEntity trainer = trainerRepository.findByUserUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException(username + "trainer does not exist"));
        trainer.getUser().setActive(active);
        trainerRepository.save(trainer);
        log.info("successfully updated active status to {} of {} trainer",active,username);
    }
    @Transactional(readOnly = true)
    public List<TrainerDTO> getTrainersNotAssignedToTrainee(String traineeUsername) {
        log.debug("Fetching trainers not assigned to trainee: {}", traineeUsername);
        List<TrainerEntity> trainers = trainerRepository.findTrainersNotAssignedToTrainee(traineeUsername);
        return trainers.stream()
                .map(trainerMapper::toDTO)
                .collect(Collectors.toList());
    }
}
