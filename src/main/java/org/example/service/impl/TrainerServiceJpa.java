package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.request.TrainerRequest;
import org.example.dto.request.TrainerTrainingsRequest;
import org.example.dto.request.TrainerUpdateRequest;
import org.example.dto.response.*;
import org.example.persistence.entity.*;
import org.example.persistence.repository.TrainerRepository;
import org.example.persistence.repository.TrainingRepository;
import org.example.persistence.repository.TrainingTypeRepository;
import org.example.persistence.repository.UserRepository;
import org.example.service.TrainerService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.example.util.NormalizeUtil.normalize;
import static org.example.util.PasswordGenerator.generatePassword;
import static org.example.util.UsernameGenerator.generateUsername;


@Service
@Slf4j
@RequiredArgsConstructor
public class TrainerServiceJpa implements TrainerService {
    private final TrainerRepository trainerRepository;
    private final UserRepository userRepository;
    private final TrainingRepository trainingRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final BCryptPasswordEncoder bcrypt;

    @Transactional
    public AuthResponse createTrainer(TrainerRequest trainerRequest){
        Set<String> availableUsernames = userRepository.findAllUserNames();
        String username = generateUsername(trainerRequest.getFirstname(),trainerRequest.getLastname(),availableUsernames);
        char[] password = generatePassword();
        String encoded = bcrypt.encode(String.valueOf(password));
        Set<TrainingTypeEntity> specializations = trainingTypeRepository.findByTrainingTypeNameIn(trainerRequest.getSpecializations());
        if(trainerRequest.getSpecializations() != null && specializations.size() != trainerRequest.getSpecializations().size())
            throw new IllegalArgumentException();
        Set<Role> roles = new HashSet<>();
        roles.add(Role.TRAINER);
        UserEntity user = UserEntity.builder()
                .firstName(trainerRequest.getFirstname())
                .userName(username)
                .passwordHash(encoded)
                .lastName(trainerRequest.getLastname())
                .isActive(true)
                .roles(roles)
                .build();
        TrainerEntity trainer = TrainerEntity.builder()
                .user(user)
                .specializations(specializations)
                .build();
        trainerRepository.save(trainer);
        return new AuthResponse(username,new String(password));
    }
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('TRAINER')")
    public TrainerProfileResponse getTrainerProfile(String username){
        log.debug("getting trainer with username : {} ", username);
        TrainerEntity trainerEntity = trainerRepository.findByUserUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("user not found " + username));
        UserEntity user  = trainerEntity.getUser();

        List<TraineeResponse> trainees =  trainerEntity.getTrainees().stream()
                .map(trainee -> TraineeResponse.builder()
                        .lastname(trainee.getUser().getLastName())
                        .firstname(trainee.getUser().getFirstName())
                        .username(trainee.getUser().getUserName())
                        .build()).toList();

        List<TrainingTypeResponse> specializations = trainerEntity.getSpecializations().stream().map(spec ->
                new TrainingTypeResponse(spec.getId(),spec.getTrainingTypeName())).toList();

        return TrainerProfileResponse.builder()
                .firstname(user.getFirstName())
                .lastname(user.getLastName())
                .specialization(specializations)
                .isActive(user.isActive())
                .trainees(trainees)
                .build();
    }

    @Transactional
    @PreAuthorize("hasRole('TRAINER')")
    public TrainerUpdateResponse updateTrainer(String username,TrainerUpdateRequest request){
        log.debug("updating trainer {}",username);
        TrainerEntity trainer = trainerRepository.findByUserUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("user does not exist + " + username));

        trainer.getUser().setFirstName(request.getFirstname());
        trainer.getUser().setLastName(request.getLastname());
        trainer.getUser().setActive(request.getIsActive());

        List<TraineeResponse> trainees = trainer.getTrainees().stream()
                .map(trainee -> TraineeResponse.builder()
                .firstname(trainee.getUser().getFirstName())
                .lastname(trainee.getUser().getLastName())
                .username(trainee.getUser().getUserName())
                .build()).toList();

        List<TrainingTypeResponse> specs = trainer.getSpecializations().stream()
                .map(spec ->
                        new TrainingTypeResponse(spec.getId(),spec.getTrainingTypeName()))
                .toList();

        log.info("updated successfully the trainer {}",username);

        return TrainerUpdateResponse.builder()
                .firstname(trainer.getUser().getFirstName())
                .isActive(trainer.getUser().isActive())
                .lastname(trainer.getUser().getLastName())
                .specialization(specs)
                .trainees(trainees)
                .username(username)
                .build();
    }

    @Transactional(readOnly = true)
    public List<TrainingResponse> getTrainerTrainings(String username,TrainerTrainingsRequest request){
        log.debug("getting trainer {} training list by creteria", username);
        if(!trainerRepository.existsByUserUserName(username))
            throw new UsernameNotFoundException("user not found " + username);
        String trainingType = normalize(request.getTrainingType());
        String traineeName = normalize(username);

        List<TrainingEntity> trainings = trainingRepository.findTrainerTrainingsByCriteria(username,request.getFrom(),request.getTo(),traineeName);
        return trainings.stream().map(training ->
                TrainingResponse.builder()
                        .traineeUsername(username)
                        .trainingType(training.getTrainingType().getTrainingTypeName())
                        .trainingDate(training.getDate())
                        .durationInMinutes(training.getTrainingDuration().toMinutesPart())
                        .trainingName(training.getTrainingName())
                        .build()).toList();
    }
}
