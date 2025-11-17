package org.example.dao.impl;
import lombok.extern.slf4j.Slf4j;
import org.example.dao.TrainerDAO;

import org.example.exception.EntityAlreadyExistException;
import org.example.exception.EntityNotFoundException;
import org.example.model.Trainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class TrainerDaoImp implements TrainerDAO {
    private Map<Long,Trainer> trainerStorage;


    @Autowired
    public void setTrainerStorage(Map<Long, Trainer> trainerStorage) {
        this.trainerStorage = trainerStorage;
    }

    @Override
    public void create(Trainer trainer) {
        Long id = trainer.getUserId();
        log.trace("DAO: create() called for trainerId={}", id);

        if (trainerStorage.containsKey(id)) {
            log.trace("DAO: trainerId={} already exists, throwing exception", id);
            throw new EntityAlreadyExistException();
        }

        trainerStorage.put(id, trainer);
        log.trace("DAO: trainerId={} created successfully", id);
    }

    @Override
    public void update(Trainer trainer) {
        Long id = trainer.getUserId();
        log.trace("DAO: update() called for trainerId={}", id);

        if (!trainerStorage.containsKey(id)) {
            log.trace("DAO: trainerId={} not found for update", id);
            throw new EntityNotFoundException("not found with id: " + id);
        }

        trainerStorage.put(id, trainer);
        log.trace("DAO: trainerId={} updated successfully", id);
    }

    @Override
    public Trainer findById(Long id) {
        log.trace("DAO: findById() called for trainerId={}", id);

        if (!trainerStorage.containsKey(id)) {
            log.trace("DAO: trainerId={} not found", id);
            throw new EntityNotFoundException("not found with id: " + id);
        }

        return trainerStorage.get(id);
    }

    @Override
    public List<Trainer> findAll() {
        log.trace("DAO: findAll() called, {} trainers found", trainerStorage.size());
        return List.copyOf(trainerStorage.values());
    }
}
