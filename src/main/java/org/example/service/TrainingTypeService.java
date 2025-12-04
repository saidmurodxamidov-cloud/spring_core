package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.TrainingTypeEntity;
import org.example.exception.EntityNotFoundException;
import org.example.mapper.TrainingTypeMapper;
import org.example.model.TrainingType;
import org.example.model.TrainingTypeDTO;
import org.example.repository.TrainingTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainingTypeService {
    private final TrainingTypeRepository trainingTypeRepository;
    private final TrainingTypeMapper trainingTypeMapper;

    @Transactional(readOnly = true)
    public TrainingTypeDTO getTrainingTypeByName(String name) {
        log.debug("Fetching training type by name: {}", name);

        TrainingTypeEntity trainingType = trainingTypeRepository.findByTrainingTypeName(name)
                .orElseThrow(() -> new EntityNotFoundException("Training type not found: " + name));

        return trainingTypeMapper.toDTO(trainingType);
    }


}
