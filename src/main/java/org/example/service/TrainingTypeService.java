package org.example.service;

import org.example.dto.request.TrainingTypeRequest;
import org.example.dto.response.TrainingTypeResponse;

import java.util.List;

public interface TrainingTypeService {

    TrainingTypeResponse createTrainingType(TrainingTypeRequest trainingTypeRequest);

    List<TrainingTypeResponse> getAllTrainingTypes();
}
