package org.example.service;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.dao.TrainingDAO;
import org.example.model.TrainingDTO;
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

    public void createTraining(@Valid TrainingDTO trainingDTO) {
        log.debug("Creating training '{}' for traineeId: {} and trainerId: {}",
                trainingDTO.getTrainingName(), trainingDTO.getTraineeId(), trainingDTO.getTrainerId());

        trainingDAO.create(trainingDTO);
        log.info("Training created with ID: {}", trainingDTO.getTrainingId());
    }

    public TrainingDTO getTrainingById(Long id) {
        log.debug("Fetching training with ID: {}", id);
        return trainingDAO.findById(id);
    }

    public List<TrainingDTO> getAllTrainings() {
        log.debug("Fetching all trainings");
        return trainingDAO.findAll();
    }
}
