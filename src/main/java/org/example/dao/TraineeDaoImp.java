package org.example.dao;

import org.example.model.Trainee;
import org.example.storage.InMemoryStorage;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TraineeDaoImp implements TraineeDAO{

    private final InMemoryStorage storage;

    public TraineeDaoImp(InMemoryStorage storage){
        this.storage = storage;
    }

    @Override
    public void create(Trainee trainee) {
        if (!storage.getTraineeStorage().containsKey(trainee.getUserId()))
            storage.getTraineeStorage().put(trainee.getUserId(), trainee);
         else
            throw new IllegalArgumentException("Trainee with ID " + trainee.getUserId() + " already exists.");

    }

    @Override
    public void update(Trainee trainee) {
        if (storage.getTraineeStorage().containsKey(trainee.getUserId()))
            storage.getTraineeStorage().put(trainee.getUserId(), trainee);
         else
            throw new IllegalArgumentException("Trainee with ID " + trainee.getUserId() + " not found.");

    }

    @Override
    public void delete(Long id) {
        storage.getTraineeStorage().remove(id);
    }

    @Override
    public Trainee findById(Long id) {
        if(storage.getTraineeStorage().containsKey(id))
            return storage.getTraineeStorage().get(id);
        throw new IllegalArgumentException("no user found with id: " + id);
    }

    @Override
    public List<Trainee> findAll() {
        return storage.getTraineeStorage().values().stream().toList();
    }
}
