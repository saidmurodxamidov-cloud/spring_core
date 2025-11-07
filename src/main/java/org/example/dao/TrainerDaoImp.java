package org.example.dao;

import lombok.RequiredArgsConstructor;
import org.example.exception.EntityAlreadyExistException;
import org.example.exception.EntityNotFoundException;
import org.example.model.Trainer;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class TrainerDaoImp implements TrainerDAO{
    private final Map<Long,Trainer> trainerStorage;


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
        return trainerStorage.values().stream().toList();
    }
}
