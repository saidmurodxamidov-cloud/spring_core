package org.example.service;

import org.example.model.TrainingTypeDTO;

import java.util.List;

public interface TrainingTypeService {

    TrainingTypeDTO create(TrainingTypeDTO trainingTypeDTO);

    List<TrainingTypeDTO> getAllTrainingTypes();
}
