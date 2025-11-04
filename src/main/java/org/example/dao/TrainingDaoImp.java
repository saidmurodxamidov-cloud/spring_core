package org.example.dao;

import lombok.RequiredArgsConstructor;
import org.example.exception.EntityAlreadyExistException;
import org.example.exception.EntityNotFoundException;
import org.example.model.Training;
import org.example.storage.TrainingStorage;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TrainingDaoImp implements TrainingDAO {
    private final TrainingStorage trainingStorage;


    @Override
    public void create(Training training) {
        if(trainingStorage.getStorage().containsKey(training.getTrainingId()))
            throw new EntityAlreadyExistException("entity already exists!");
        trainingStorage.getStorage().put(training.getTrainingId(),training);
    }

    @Override
    public Training findById(Long id) {
        if(!trainingStorage.getStorage().containsKey(id))
            throw new EntityNotFoundException("entity not found with id: " + id);
        return trainingStorage.getStorage().get(id);
    }


    @Override
    public List<Training> findAll() {
        return trainingStorage.getStorage().values().stream().toList();
    }
}
