package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.request.TrainerRegistrationRequest;
import org.example.dto.response.AuthResponse;
import org.example.exception.EntityNotFoundException;
import org.example.persistence.entity.Role;
import org.example.persistence.entity.TrainerEntity;
import org.example.mapper.TrainerMapper;
import org.example.persistence.entity.TrainingTypeEntity;
import org.example.persistence.model.TrainerDTO;
import org.example.persistence.model.TrainingTypeDTO;
import org.example.persistence.repository.TrainerRepository;
import org.example.persistence.repository.TrainingTypeRepository;
import org.example.persistence.repository.UserRepository;
import org.example.service.TrainerService;
import org.example.util.PasswordGenerator;
import org.example.util.UsernameGenerator;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.example.util.PasswordGenerator.generatePassword;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainerServiceJpa implements TrainerService {

    private final TrainerRepository trainerRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bcrypt;
    private final TrainerMapper trainerMapper;
    private final TrainingTypeRepository trainingTypeRepository;
    @Transactional
    public AuthResponse createTrainer(TrainerRegistrationRequest trainerRegistrationRequest){
        Set<String> availableUsernames = userRepository.findAllUserNames();
        String password = String.valueOf(generatePassword());
        String encoded = bcrypt.encode(password);
        String username = UsernameGenerator.generateUsername(trainerRegistrationRequest.getFirstname(),trainerRegistrationRequest.getLastname(),availableUsernames);
        Set<TrainingTypeEntity> trainingTypes = trainingTypeRepository.findByTrainingTypeNameIn(trainerRegistrationRequest.getSpecialization());
        if(trainingTypes.size() != trainerRegistrationRequest.getSpecialization().size()){
            log.error("some training types are missing, does not exist");
            throw new IllegalArgumentException("some training types are missing, does not exist");
        }
        log.info("creating trainer with username {}", username);
        TrainerDTO trainerDTO = new TrainerDTO();
        trainerDTO.setUserName(username);
        trainerDTO.setFirstName(trainerRegistrationRequest.getFirstname());
        trainerDTO.setLastName(trainerRegistrationRequest.getLastname());

        TrainerEntity trainer = trainerMapper.toEntity(trainerDTO);
        trainer.getUser().setRoles(new HashSet<>());
        trainer.getUser().setPasswordHash(encoded);
        trainer.getUser().getRoles().add(Role.TRAINER);
        trainer.setSpecializations(trainingTypes);
        trainer.getUser().setActive(true);
        trainerRepository.save(trainer);
        log.info("trainer {} created successfully", username);
        return new AuthResponse(username,password);
    }
    @PreAuthorize("hasRole('TRAINER') or hasRole('ADMIN')")
    @Transactional
    public TrainerDTO getTrainerByUsername(String username){
        log.debug("getting trainer: {}" , username);
        TrainerEntity trainer = trainerRepository.findByUserUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("trainer " + username + " does not exist"));
        return trainerMapper.toDTO(trainer);
    }
    @Transactional
//    @PreAuthorize("hasRole('TRAINER') or hasRole('ADMIN')")
    public TrainerDTO updateTrainer(TrainerDTO trainerDto){
        log.debug("updating trainer: {}", trainerDto.getUserName());
        TrainerEntity trainerEntity = trainerRepository.findByUserUserName(trainerDto.getUserName())
                .orElseThrow(() -> new UsernameNotFoundException("trainer: " + trainerDto.getUserName() + " does not exist"));
        trainerMapper.updateFromDTO(trainerDto,trainerEntity);
        trainerRepository.save(trainerEntity);
        log.info("trainer {} updated successfully", trainerDto.getUserName());
        return trainerDto;
    }

    @Transactional(readOnly = true)
//    @PreAuthorize("hasRole('TRAINER') or hasRole('ADMIN')")
    public List<TrainerDTO> getTrainersNotAssignedToTrainee(String traineeUsername) {
        log.debug("Fetching trainers not assigned to trainee: {}", traineeUsername);
        List<TrainerEntity> trainers = trainerRepository.findTrainersNotAssignedToTrainee(traineeUsername);
        return trainers.stream()
                .map(trainerMapper::toDTO)
                .collect(Collectors.toList());
    }
}
