package org.example.dao;

import org.example.model.Trainer;
import org.example.storage.InMemoryStorage;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class TrainerDaoImp implements TrainerDAO{
    private final InMemoryStorage storage;

    public TrainerDaoImp(InMemoryStorage storage) {
        this.storage = storage;
    }
    @Override
    public void create(Trainer trainer) {
        if (!storage.getTrainerStorage().containsKey(trainer.getUserId())) {
            storage.getTrainerStorage().put(trainer.getUserId(), trainer);
        } else {
            throw new IllegalArgumentException("Trainer with ID " + trainer.getUserId() + " already exists.");
        }
    }

    @Override
    public void update(Trainer trainer) {
        if (storage.getTrainerStorage().containsKey(trainer.getUserId())) {
            storage.getTrainerStorage().put(trainer.getUserId(), trainer);
        } else {
            throw new IllegalArgumentException("Trainer with ID " + trainer.getUserId() + " not found.");
        }
    }

    @Override
    public void delete(Long id) {
        storage.getTrainerStorage().remove(id);
    }

    @Override
    public Trainer findById(Long id) {
        return storage.getTrainerStorage().get(id);
    }

    @Override
    public List<Trainer> findAll() {
        return new ArrayList<>(storage.getTrainerStorage().values());
    }
}
