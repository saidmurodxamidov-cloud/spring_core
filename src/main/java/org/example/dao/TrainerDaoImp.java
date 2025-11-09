package org.example.dao;

import lombok.RequiredArgsConstructor;
import org.example.exception.EntityAlreadyExistException;
import org.example.exception.EntityNotFoundException;
import org.example.model.Trainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository

public class TrainerDaoImp implements TrainerDAO{
    private Map<Long,Trainer> trainerStorage;

    @Autowired
    public void setTrainerStorage(Map<Long, Trainer> trainerStorage) {
        this.trainerStorage = trainerStorage;
    }

    @Override
    public void create(Trainer trainer) {
        if (trainerStorage.containsKey(trainer.getUserId())) {
            throw new EntityAlreadyExistException();
        }
        trainerStorage.put(trainer.getUserId(), trainer);
    }

    @Override
    public void update(Trainer trainer) {
        if(!trainerStorage.containsKey(trainer.getUserId()))
            throw new EntityNotFoundException("not found with id: " + trainer.getUserId());
        trainerStorage.put(trainer.getUserId(),trainer);
    }

    @Override
    public Trainer findById(Long id) {
        if(!trainerStorage.containsKey(id))
            throw new EntityNotFoundException("not found with id: " + id);
        return trainerStorage.get(id);
    }

    @Override
    public List<Trainer> findAll() {
        return List.copyOf(trainerStorage.values());
    }
}
