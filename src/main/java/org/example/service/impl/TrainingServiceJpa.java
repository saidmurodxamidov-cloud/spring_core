package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.request.TrainingAddRequest;
import org.example.persistence.entity.TraineeEntity;
import org.example.persistence.entity.TrainerEntity;
import org.example.persistence.entity.TrainingEntity;
import org.example.persistence.entity.TrainingTypeEntity;
import org.example.persistence.repository.TraineeRepository;
import org.example.persistence.repository.TrainerRepository;
import org.example.persistence.repository.TrainingRepository;
import org.example.persistence.repository.TrainingTypeRepository;
import org.example.service.TrainingService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainingServiceJpa implements TrainingService {
    private final TrainingRepository trainingRepository;
    private final TrainerRepository trainerRepository;
    private final TraineeRepository traineeRepository;
    private final TrainingTypeRepository trainingTypeRepository;

    @Transactional
    public void addTraining(TrainingAddRequest request){
        TraineeEntity trainee = traineeRepository.findByUserUserName(request.getTraineeUsername())
                .orElseThrow(() -> new UsernameNotFoundException("trainee does not exist " + request.getTraineeUsername()));
        TrainerEntity trainer = trainerRepository.findByUserUserName(request.getTrainerUsername())
                .orElseThrow(() -> new UsernameNotFoundException("trainer does not exist " + request.getTraineeUsername()));
        TrainingTypeEntity trainingType = trainingTypeRepository.findByTrainingTypeName(request.getTrainingType())
                .orElseThrow(() -> new IllegalArgumentException("training does not exist " + request.getTrainingType()));
        trainer.getTrainees().add(trainee);
        trainee.getTrainers().add(trainer);
        TrainingEntity training = TrainingEntity.builder()
                .trainingDuration(Duration.ofMinutes(request.getTrainingDurationInMinutes()))
                .date(request.getTrainingDate())
                .trainingName(request.getTrainingName())
                .trainingType(trainingType)
                .trainee(trainee)
                .trainer(trainer)
                .build();
        trainingRepository.save(training);
        trainer.getTrainings().add(training);
        trainee.getTrainings().add(training);
        log.info("successfully created training with name {}", training.getTrainingName());
    }

}
