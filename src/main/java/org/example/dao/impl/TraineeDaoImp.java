package org.example.dao.impl;
import lombok.extern.slf4j.Slf4j;
import org.example.dao.TraineeDAO;

import org.example.exception.EntityAlreadyExistException;
import org.example.exception.EntityNotFoundException;
import org.example.model.Trainee;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class TraineeDaoImp implements TraineeDAO {

    private Map<Long, Trainee> traineeStorage;

    @Override
    public void create(Trainee trainee) {
        Long id = trainee.getUserId();

        log.trace("DAO: create() called for traineeId={}", id);

        if (traineeStorage.containsKey(id)) {
            log.trace("DAO: traineeId={} already exists", id);
            throw new EntityAlreadyExistException();
        }

        traineeStorage.put(id, trainee);
        log.trace("DAO: traineeId={} created successfully", id);
    }

    @Override
    public void update(Trainee trainee) {
        Long id = trainee.getUserId();
        log.trace("DAO: update() called for traineeId={}", id);

        if (!traineeStorage.containsKey(id)) {
            log.trace("DAO: traineeId={} not found for update", id);
            throw new EntityNotFoundException();
        }

        traineeStorage.put(id, trainee);
        log.trace("DAO: traineeId={} updated successfully", id);
    }

    @Override
    public void delete(Long id) {
        log.trace("DAO: delete() called for traineeId={}", id);

        if (!traineeStorage.containsKey(id)) {
            log.trace("DAO: traineeId={} not found for delete", id);
            throw new EntityNotFoundException();
        }

        traineeStorage.remove(id);
        log.trace("DAO: traineeId={} deleted", id);
    }

    @Override
    public Trainee findById(Long id) {
        log.trace("DAO: findById() called for traineeId={}", id);

        if (!traineeStorage.containsKey(id)) {
            log.trace("DAO: traineeId={} not found", id);
            throw new EntityNotFoundException();
        }

        return traineeStorage.get(id);
    }

    @Override
    public List<Trainee> findAll() {
        log.trace("DAO: findAll() called, {} trainees found", traineeStorage.size());
        return List.copyOf(traineeStorage.values());
    }
}
