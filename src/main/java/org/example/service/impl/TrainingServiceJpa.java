package org.example.service.impl;

import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.messageQueue.ActionType;

import org.example.messageQueue.WorkloadSender;
import org.example.dto.request.TrainerWorkloadRequest;
import org.example.dto.request.TrainingAddRequest;
import org.example.exception.EntityNotFoundException;
import org.example.mapper.TrainerWorkloadMapper;
import org.example.mapper.TrainingMapper;
import org.example.metrics.MetricsService;
import org.example.persistence.entity.TraineeEntity;
import org.example.persistence.entity.TrainerEntity;
import org.example.persistence.entity.TrainingEntity;
import org.example.persistence.entity.TrainingTypeEntity;
import org.example.persistence.model.TrainingDTO;
import org.example.persistence.repository.TraineeRepository;
import org.example.persistence.repository.TrainerRepository;
import org.example.persistence.repository.TrainingRepository;
import org.example.persistence.repository.TrainingTypeRepository;
import org.example.service.TrainingService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainingServiceJpa implements TrainingService {
    private final TrainingRepository trainingRepository;
    private final TrainerRepository trainerRepository;
    private final TraineeRepository traineeRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final MetricsService metricsService;
    private final WorkloadSender workloadSenderService;
    private final TrainerWorkloadMapper workloadMapper;
    private final TrainingMapper trainingMapper;

    @Transactional
    public TrainingDTO addTraining(TrainingAddRequest request){
        Timer.Sample sample = metricsService.startTrainingCreationTimer();
        try{
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

        TrainerWorkloadRequest workloadRequest = workloadMapper.toDto(training,ActionType.ADD);
        submitWorkLoadEvent(workloadRequest);

        metricsService.incrementTrainingCreated();
        log.info("successfully created training with name {}", training.getTrainingName());
        return trainingMapper.toTraining(training);

        }finally{
            metricsService.recordTrainingCreationDuration(sample);
        }
    }

    private void submitWorkLoadEvent(TrainerWorkloadRequest request){
        String uuid = UUID.randomUUID().toString();
        workloadSenderService.sendWorkload(uuid,request);
    }

    @Transactional
    public void deleteTraining(Long trainingId){
        if(!trainingRepository.existsById(trainingId)){
            return;
        }
        TrainingEntity trainingEntity = trainingRepository.findById(trainingId).orElseThrow(EntityNotFoundException::new);
        trainingRepository.delete(trainingEntity);

        TrainerWorkloadRequest workloadRequest = workloadMapper.toDto(trainingEntity,ActionType.DELETE);

        submitWorkLoadEvent(workloadRequest);

    }
}
