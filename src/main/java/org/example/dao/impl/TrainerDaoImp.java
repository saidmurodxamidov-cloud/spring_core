package org.example.dao.impl;
import lombok.extern.slf4j.Slf4j;
import org.example.dao.TrainerDAO;

import org.example.exception.EntityAlreadyExistException;
import org.example.exception.EntityNotFoundException;
import org.example.model.TrainerDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class TrainerDaoImp implements TrainerDAO {
    private Map<Long, TrainerDTO> trainerStorage;


    @Autowired
    public void setTrainerStorage(Map<Long, TrainerDTO> trainerStorage) {
        this.trainerStorage = trainerStorage;
    }

    @Override
    public void create(TrainerDTO trainerDTO) {
        Long id = trainerDTO.getUserId();
        log.trace("DAO: create() called for trainerId={}", id);

        if (trainerStorage.containsKey(id)) {
            log.trace("DAO: trainerId={} already exists, throwing exception", id);
            throw new EntityAlreadyExistException();
        }

        trainerStorage.put(id, trainerDTO);
        log.trace("DAO: trainerId={} created successfully", id);
    }

    @Override
    public void update(TrainerDTO trainerDTO) {
        Long id = trainerDTO.getUserId();
        log.trace("DAO: update() called for trainerId={}", id);

        if (!trainerStorage.containsKey(id)) {
            log.trace("DAO: trainerId={} not found for update", id);
            throw new EntityNotFoundException("not found with id: " + id);
        }

        trainerStorage.put(id, trainerDTO);
        log.trace("DAO: trainerId={} updated successfully", id);
    }

    @Override
    public TrainerDTO findById(Long id) {
        log.trace("DAO: findById() called for trainerId={}", id);

        if (!trainerStorage.containsKey(id)) {
            log.trace("DAO: trainerId={} not found", id);
            throw new EntityNotFoundException("not found with id: " + id);
        }

        return trainerStorage.get(id);
    }

    @Override
    public List<TrainerDTO> findAll() {
        log.trace("DAO: findAll() called, {} trainers found", trainerStorage.size());
        return List.copyOf(trainerStorage.values());
    }
}
