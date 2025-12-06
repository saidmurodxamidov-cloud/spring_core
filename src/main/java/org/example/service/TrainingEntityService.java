package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.TraineeEntity;
import org.example.entity.TrainerEntity;
import org.example.entity.TrainingEntity;
import org.example.entity.TrainingTypeEntity;
import org.example.exception.EntityNotFoundException;
import org.example.model.TrainingDTO;
import org.example.repository.TraineeRepository;
import org.example.repository.TrainerRepository;
import org.example.repository.TrainingRepository;
import org.example.repository.TrainingTypeRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainingEntityService {

    private final TrainingRepository trainingRepository;
    private final TrainerRepository trainerRepository;
    private final TraineeRepository traineeRepository;
    private final TrainingTypeRepository trainingTypeRepository;

    @Transactional
    TrainingDTO createTraining(TrainingDTO trainingDTO){
        log.info("Creating training with name {}", trainingDTO.getTrainingName());
        TraineeEntity trainee = traineeRepository.findById((trainingDTO.getTraineeId()))
                .orElseThrow(() -> new UsernameNotFoundException("trainee with id: " + trainingDTO.getTrainingId() + " does not exist"));

        TrainerEntity trainer = trainerRepository.findById(trainingDTO.getTrainerId())
                .orElseThrow(() -> new UsernameNotFoundException("trainer with id: " + trainingDTO.getTrainerId() + " not found"));

        TrainingTypeEntity trainingType = trainingTypeRepository.findByTrainingTypeName(trainingDTO.getTrainingType().getTrainingTypeName())
                .orElseThrow(EntityNotFoundException::new);

        TrainingEntity training = TrainingEntity.builder()
                .trainee(trainee)
                .trainer(trainer)
                .trainingName(trainingDTO.getTrainingName())
                .trainingType(trainingType)
                .trainingDuration(trainingDTO.getTrainingDuration())
                .date(trainingDTO.getDate())
                .build();

        trainingRepository.save(training);
        log.info("training named {} created successfully", training.getTrainingName());
        return trainingDTO;
    }
}
