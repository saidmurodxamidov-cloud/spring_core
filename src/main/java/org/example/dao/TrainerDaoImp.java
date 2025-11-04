package org.example.dao;

import lombok.RequiredArgsConstructor;
import org.example.exception.EntityAlreadyExistException;
import org.example.exception.EntityNotFoundException;
import org.example.model.Trainer;
import org.example.storage.TrainerStorage;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class TrainerDaoImp implements TrainerDAO{
    private final TrainerStorage trainerStorage;


    @Override
    public void create(Trainer trainer) {
        if (trainerStorage.getStorage().containsKey(trainer.getUserId())) {
            throw new EntityAlreadyExistException();
        }
        trainerStorage.getStorage().put(trainer.getUserId(), trainer);
    }

    @Override
    public void update(Trainer trainer) {
        if(!trainerStorage.getStorage().containsKey(trainer.getUserId()))
            throw new EntityNotFoundException("not found with id: " + trainer.getUserId());
        trainerStorage.getStorage().put(trainer.getUserId(),trainer);
    }

    @Override
    public Trainer findById(Long id) {
        if(!trainerStorage.getStorage().containsKey(id))
            throw new EntityNotFoundException("not found with id: " + id);
        return trainerStorage.getStorage().get(id);
    }

    @Override
    public List<Trainer> findAll() {
        return trainerStorage.getStorage().values().stream().toList();
    }
}
