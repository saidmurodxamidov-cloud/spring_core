package org.example.dao;

import lombok.extern.slf4j.Slf4j;
import org.example.exception.EntityAlreadyExistException;
import org.example.exception.EntityNotFoundException;
import org.example.model.Training;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class TrainingDaoImp implements TrainingDAO {

    private Map<Long, Training> trainingStorage;

    @Autowired
    public void setTrainingStorage(Map<Long, Training> trainingStorage) {
        this.trainingStorage = trainingStorage;
    }

    @Override
    public void create(Training training) {
        Long id = training.getTrainingId();
        log.trace("DAO: create() called for trainingId={}", id);

        if (trainingStorage.containsKey(id)) {
            log.trace("DAO: trainingId={} already exists, throwing exception", id);
            throw new EntityAlreadyExistException("entity already exists!");
        }

        trainingStorage.put(id, training);
        log.trace("DAO: trainingId={} created successfully", id);
    }

    @Override
    public Training findById(Long id) {
        log.trace("DAO: findById() called for trainingId={}", id);

        if (!trainingStorage.containsKey(id)) {
            log.trace("DAO: trainingId={} not found", id);
            throw new EntityNotFoundException("entity not found with id: " + id);
        }

        return trainingStorage.get(id);
    }

    @Override
    public List<Training> findAll() {
        log.trace("DAO: findAll() called, {} trainings found", trainingStorage.size());
        return List.copyOf(trainingStorage.values());
    }
}
