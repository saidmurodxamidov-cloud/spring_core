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
    public List<Training> findAll() {
        return storage.getTrainingStorage().stream().toList();
    }
}
