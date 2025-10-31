package org.example.dao;

import org.example.model.Training;
import org.example.storage.InMemoryStorage;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TrainingDaoImp implements TrainingDAO {
    private final InMemoryStorage storage;

    public TrainingDaoImp(InMemoryStorage storage) {
        this.storage = storage;
    }

    @Override
    public void create(Training training) {
        storage.getTrainingStorage().add(training);
    }

    @Override
    public void update(Training trainee) {
        var old = findById(trainee.getTraineeId());
        if(storage.getTrainingStorage().contains(old)){
            storage.getTrainingStorage().remove(old);
            storage.getTrainingStorage().add(trainee);
        }else
            throw new IllegalArgumentException();
    }


    @Override
    public Training findById(Long id) {
        return null;
    }

    @Override
    public void delete(Training training) {
        storage.getTrainingStorage().remove(training);
    }

    @Override
    public List<Training> findAll() {
        return storage.getTrainingStorage().stream().toList();
    }
}
