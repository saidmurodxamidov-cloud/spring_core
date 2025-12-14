package org.example.service.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.TrainingTypeEntity;
import org.example.exception.EntityNotFoundException;
import org.example.mapper.TrainingTypeMapper;
import org.example.model.TrainingTypeDTO;
import org.example.repository.TrainingTypeRepository;
import org.example.service.TrainingTypeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainingTypeServiceJpa implements TrainingTypeService {
    private final TrainingTypeMapper trainingTypeMapper;
    private final TrainingTypeRepository trainingTypeRepository;

    @Transactional
    public TrainingTypeDTO create(TrainingTypeDTO trainingTypeDTO){
        log.debug("creating training type with name: {}", trainingTypeDTO.getTrainingTypeName());
        if(trainingTypeRepository.existsByTrainingTypeName(trainingTypeDTO.getTrainingTypeName()))
            return trainingTypeRepository.findByTrainingTypeName(trainingTypeDTO.getTrainingTypeName()).map(trainingTypeMapper::toDTO).orElseThrow();
        TrainingTypeEntity trainingType = trainingTypeMapper.toEntity(trainingTypeDTO);
        trainingTypeRepository.save(trainingType);
        log.info("training type created successfully {}", trainingTypeDTO.getTrainingTypeName());
        return trainingTypeMapper.toDTO(trainingType);
    }

    @Transactional(readOnly = true)
    public List<TrainingTypeDTO> getAllTrainingTypes() {
        return trainingTypeRepository.findAll().stream().map(trainingTypeMapper::toDTO).toList();
    }

}
