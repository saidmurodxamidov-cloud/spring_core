package org.example.service.dao;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.dao.TrainingDAO;
import org.example.model.Training;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@Slf4j
@Validated
public class TrainingService {

    private TrainingDAO trainingDAO;

    @Autowired
    public void setTrainingDAO(TrainingDAO trainingDAO) {
        this.trainingDAO = trainingDAO;
    }

    public void createTraining(@Valid Training training) {
        log.debug("Creating training '{}' for traineeId: {} and trainerId: {}",
                training.getTrainingName(), training.getTraineeId(), training.getTrainerId());

        trainingDAO.create(training);
        log.info("Training created with ID: {}", training.getTrainingId());
    }

    public Training getTrainingById(Long id) {
        log.debug("Fetching training with ID: {}", id);
        return trainingDAO.findById(id);
    }

    public List<Training> getAllTrainings() {
        log.debug("Fetching all trainings");
        return trainingDAO.findAll();
    }
}
