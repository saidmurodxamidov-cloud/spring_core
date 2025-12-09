package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.TraineeEntity;
import org.example.entity.TrainerEntity;
import org.example.entity.TrainingEntity;
import org.example.entity.TrainingTypeEntity;
import org.example.exception.EntityNotFoundException;
import org.example.mapper.TrainingMapper;
import org.example.model.TrainingDTO;
import org.example.repository.TraineeRepository;
import org.example.repository.TrainerRepository;
import org.example.repository.TrainingRepository;
import org.example.repository.TrainingTypeRepository;
import org.example.service.TrainingService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.example.util.NormalizeUtil.normalize;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainingServiceJpa implements TrainingService {

    private final TrainingRepository trainingRepository;
    private final TrainerRepository trainerRepository;
    private final TraineeRepository traineeRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final TrainingMapper trainingMapper;

    @Transactional
    public TrainingDTO createTraining(TrainingDTO trainingDTO){
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
    @Transactional(readOnly = true)
    public List<TrainingDTO> getAllTraineeTrainings(String username){
        log.debug("getting trainee: {}'s trainings",username);
        TraineeEntity trainee = traineeRepository.findByUserUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("trainee " +  username + " does not exist"));
        log.debug("successfully gotten trainings for trainee {}",username);
        return trainingMapper.toTrainingModels(trainee.getTrainings()).stream().toList();
    }
    @Transactional(readOnly = true)
    public List<TrainingDTO> getAllTrainerTrainings(String username){
        log.debug("getting trainer: {}'s trainings",username);
        TrainerEntity trainee = trainerRepository.findByUserUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("trainer " +  username + " does not exist"));
        log.debug("successfully gotten trainings for trainer {}",username);
        return trainingMapper.toTrainingModels(trainee.getTrainings()).stream().toList();
    }

    @Transactional(readOnly = true)
    public List<TrainingDTO> getTraineeTrainings(String username, LocalDate fromDate, LocalDate toDate, String trainerName, String trainingTypeName){
        log.debug("getting trainee {}, from = {},to = {}, trainerName = {}, trainingTypeName = {} trainings",username,fromDate,toDate,trainerName,trainingTypeName);

        if(!traineeRepository.existsByUserUserName(username))
            throw new UsernameNotFoundException("trainee " + username + " does not exist");

        trainerName = normalize(trainerName);
        trainingTypeName = normalize(trainingTypeName);
        log.debug("got successfully trainee {}, from = {},to = {}, trainerName = {}, trainingTypeName = {} trainings",username,fromDate,toDate,trainerName,trainingTypeName);

        return trainingRepository.findTraineeTrainingsByCriteria(
                username,
                fromDate,
                toDate,
                trainerName,
                trainingTypeName
        ).stream().map(trainingMapper::toTraining).toList();
    }

    @Transactional(readOnly = true)
    public List<TrainingDTO> getTrainerTrainings(String username,
                                                 LocalDate fromDate,
                                                 LocalDate toDate,
                                                 String traineeName) {
        if(!trainerRepository.existsByUserUserName(username))
            throw new UsernameNotFoundException("trainer " + username + " does not exist");
        log.debug("getting trainer {}, from = {},to = {}, traineeName = {} trainings",username,fromDate,toDate,traineeName);
        traineeName = normalize(traineeName);
        List<TrainingEntity> trainings = trainingRepository.findTrainerTrainingsByCriteria(
                username, fromDate, toDate, traineeName
        );
        log.debug("got successfully trainer {}, from = {},to = {}, traineeName = {} trainings",username,fromDate,toDate,traineeName);

        return trainings.stream().map(trainingMapper::toTraining).toList();
    }
}
