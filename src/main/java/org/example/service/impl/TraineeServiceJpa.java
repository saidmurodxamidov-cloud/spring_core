package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.request.TraineeRegistrationRequest;
import org.example.dto.request.TraineeTrainingRequest;
import org.example.dto.request.TraineeUpdateRequest;
import org.example.dto.response.*;
import org.example.persistence.entity.*;
import org.example.persistence.repository.TraineeRepository;
import org.example.persistence.repository.TrainerRepository;
import org.example.persistence.repository.TrainingRepository;
import org.example.persistence.repository.UserRepository;
import org.example.service.TraineeService;
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
@RequiredArgsConstructor
@Slf4j
public class TraineeServiceJpa implements TraineeService {
    private final TraineeRepository traineeRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bcrypt;
    private final TrainerRepository trainerRepository;
    private final TrainingRepository trainingRepository;

    @Transactional
    public AuthResponse createTrainee(TraineeRegistrationRequest traineeRequest){
        Set<String> availableUsernames = userRepository.findAllUserNames();
        String username = generateUsername(traineeRequest.getFirstname(),traineeRequest.getLastname(),availableUsernames);
        log.debug("creating trainee with username: {}",username);
        Set<Role> roles = new HashSet<>();
        roles.add(Role.TRAINEE);
        char[] password = generatePassword();
        String encodedPassword = bcrypt.encode(String.valueOf(password));
        UserEntity user = UserEntity.builder()
                .firstName(traineeRequest.getFirstname())
                .lastName(traineeRequest.getLastname())
                .userName(username)
                .passwordHash(encodedPassword)
                .roles(roles)
                .build();
        TraineeEntity trainee = TraineeEntity.builder()
                .user(user)
                .address(traineeRequest.getAddress())
                .dateOfBirth(traineeRequest.getDateOfBirth())
                .build();
        traineeRepository.save(trainee);

        return new AuthResponse(username,String.valueOf(password));
    }
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('TRAINEE')")
    public TraineeProfileResponse getTrainee(String username){
        log.debug("getting trainee profile {}", username);
        TraineeEntity trainee = traineeRepository.findByUserUserName(username)
                .orElseThrow( () -> new UsernameNotFoundException(username + " user does not exist!"));

        List<TrainerResponse> trainers = trainee.getTrainers().stream().map(trainer -> {
            List<TrainingTypeResponse> specs = trainer.getSpecializations().stream()
                    .map(spec -> new TrainingTypeResponse(spec.getId(),spec.getTrainingTypeName())).toList();
            return TrainerResponse.builder()
                    .lastname(trainer.getUser().getLastName())
                    .firstname(trainer.getUser().getFirstName())
                    .username(trainer.getUser().getUserName())
                    .trainingTypeResponse(specs)
                    .build();
        }).toList();

        return TraineeProfileResponse.builder()
                .trainers(trainers)
                .isActive(trainee.getUser().isActive())
                .firstname(trainee.getUser().getFirstName())
                .lastname(trainee.getUser().getLastName())
                .dateOfBirth(trainee.getDateOfBirth())
                .address(trainee.getAddress())
                .build();
    }
    @Transactional
    @PreAuthorize("hasRole('TRAINEE')")
    public TraineeUpdateResponse updateTrainee(String username,TraineeUpdateRequest request) {

        TraineeEntity trainee = traineeRepository.findByUserUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("Trainee not found"));

        trainee.getUser().setFirstName(request.getFirstname());
        trainee.getUser().setLastName(request.getLastname());
        trainee.getUser().setActive(request.getIsActive());

        if (request.getAddress() != null) {
            trainee.setAddress(request.getAddress());
        }
        if (request.getDateOfBirth() != null) {
            trainee.setDateOfBirth(request.getDateOfBirth());
        }

        return TraineeUpdateResponse.builder()
                .username(trainee.getUser().getUserName())
                .firstname(trainee.getUser().getUserName())
                .lastname(trainee.getUser().getLastName())
                .dateOfBirth(trainee.getDateOfBirth())
                .address(trainee.getAddress())
                .isActive(trainee.getUser().isActive())
                .trainers(trainee.getTrainers().stream()
                        .map(trainer -> {
                            List<TrainingTypeResponse> list = trainer.getSpecializations().stream()
                                    .map(spec -> new TrainingTypeResponse(spec.getId(),spec.getTrainingTypeName())).toList();
                            return TrainerResponse.builder()
                                    .lastname(trainer.getUser().getLastName())
                                    .firstname(trainer.getUser().getFirstName())
                                    .username(trainer.getUser().getUserName())
                                    .trainingTypeResponse(list)
                                    .build();
                        }).toList()).build();
    }

    @Transactional
    @PreAuthorize("hasRole('TRAINEE')")
    public void deleteTrainee(String username){
        log.debug("deleting trainee with username {}" , username);
         TraineeEntity trainee = traineeRepository.findByUserUserName(username).orElseThrow(() -> new UsernameNotFoundException("user does not exist " + username));
         traineeRepository.delete(trainee);
         log.info("trainee {} deleted success",username);
    }

    @Transactional
    public List<TrainerResponse> updateTraineesTrainerList(String username, List<String> trainerUsernames){
        TraineeEntity trainee = traineeRepository.findByUserUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found " + username));
        Set<TrainerEntity> newTrainers =  trainerRepository.findByUserUserNameIn(trainerUsernames);
        if(newTrainers.size() != trainerUsernames.size())
            log.warn("some of trainers referenced by trainee {} not found",  username);
        trainee.setTrainers(newTrainers);
        traineeRepository.save(trainee);

        return newTrainers.stream().map(trainer -> {
            List<TrainingTypeResponse> specs = trainer.getSpecializations().stream()
                    .map(spec -> new TrainingTypeResponse(spec.getId(),spec.getTrainingTypeName())).toList();
            return TrainerResponse.builder()
                    .trainingTypeResponse(specs)
                    .firstname(trainer.getUser().getFirstName())
                    .lastname(trainer.getUser().getLastName())
                    .username(trainer.getUser().getUserName())
                    .build();
        }).toList();
    }
    @Transactional(readOnly = true)
    public List<TrainingResponse> getTraineeTrainings(String username, TraineeTrainingRequest request){
        log.debug("getting trainee {} trainers list by creteria", request.getTrainerName());
        if(!traineeRepository.existsByUserUserName(username))
            throw new UsernameNotFoundException("user not found " + username);
        String trainingType = normalize(request.getTrainingType());
        String trainerUsername = normalize(request.getTrainerName());

        List<TrainingEntity> trainings = trainingRepository.findTraineeTrainingsByCriteria(username,request.getFrom(),request.getTo(),trainerUsername,trainingType);
        return trainings.stream().map(training ->
            TrainingResponse.builder()
                    .traineeUsername(username)
                    .trainingType(training.getTrainingType().getTrainingTypeName())
                    .trainingDate(training.getDate())
                    .durationInMinutes((int) training.getTrainingDuration().toMinutes())
                    .trainingName(training.getTrainingName())
                    .build()).toList();
    }
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('TRAINER') or hasRole('TRAINER')")
    public List<TrainerResponse> getActiveTrainersNotAssignedToTrainee(String username){
        log.debug("getting active trainer not assigned to trainee {}", username);
        List<TrainerEntity> trainers = trainerRepository.findTrainersNotAssignedToTrainee(username);
        return trainers.stream().map(trainer -> {
            List<TrainingTypeResponse> specs = trainer.getSpecializations().stream()
                    .map(spec -> new TrainingTypeResponse(spec.getId(),spec.getTrainingTypeName())).toList();
            return TrainerResponse.builder()
                    .firstname(trainer.getUser().getFirstName())
                    .lastname(trainer.getUser().getLastName())
                    .username(trainer.getUser().getUserName())
                    .trainingTypeResponse(specs)
                    .build();
        }).toList();
    }
}
