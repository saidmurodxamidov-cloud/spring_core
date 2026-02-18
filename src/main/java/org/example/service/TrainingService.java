package org.example.service;

import org.example.dto.request.TrainingAddRequest;
import org.example.persistence.model.TrainingDTO;

public interface TrainingService {

    TrainingDTO addTraining(TrainingAddRequest request);
    void deleteTraining(Long training_id);
}
