package org.example.dao;

import lombok.RequiredArgsConstructor;
import org.example.exception.EntityAlreadyExistException;
import org.example.exception.EntityNotFoundException;
import org.example.model.Training;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class TrainingDaoImp implements TrainingDAO {
    private Map<Long,Training> trainingStorage;

    @Autowired
    public void setTrainingStorage(Map<Long, Training> trainingStorage) {
        this.trainingStorage = trainingStorage;
    }
    @Override
    public void create(Training training) {
        if(trainingStorage.containsKey(training.getTrainingId()))
            throw new EntityAlreadyExistException("entity already exists!");
        trainingStorage.put(training.getTrainingId(),training);
    }

    @Override
    public Training findById(Long id) {
        if(!trainingStorage.containsKey(id))
            throw new EntityNotFoundException("entity not found with id: " + id);
        return trainingStorage.get(id);
    }


    @Override
    public List<Training> findAll() {
        return List.copyOf(trainingStorage.values());
    }
}
