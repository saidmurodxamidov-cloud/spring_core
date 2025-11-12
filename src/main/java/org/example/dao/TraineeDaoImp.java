package org.example.dao;

import lombok.RequiredArgsConstructor;
import org.example.exception.EntityAlreadyExistException;
import org.example.exception.EntityNotFoundException;
import org.example.model.Trainee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class TraineeDaoImp implements TraineeDAO{

    private Map<Long,Trainee> traineeStorage;

    @Autowired
    public void setTraineeStorage(Map<Long, Trainee> traineeStorage) {
        this.traineeStorage = traineeStorage;
    }

    @Override
    public void create(Trainee trainee) {
        if (traineeStorage.containsKey(trainee.getUserId()))
            throw new EntityAlreadyExistException();

        traineeStorage.put(trainee.getUserId(),trainee);
    }

    @Override
    public void update(Trainee trainee) {
        if (!traineeStorage.containsKey(trainee.getUserId()))
            throw new EntityNotFoundException();
        traineeStorage.put(trainee.getUserId(), trainee);

    }

    @Override
    public void delete(Long id) {
        if(!traineeStorage.containsKey(id))
            throw new EntityNotFoundException();
        traineeStorage.remove(id);
    }

    @Override
    public Trainee findById(Long id) {
        if(!traineeStorage.containsKey(id))
            throw new EntityNotFoundException();
        return traineeStorage.get(id);
    }

    @Override
    public List<Trainee> findAll() {
        return List.copyOf(traineeStorage.values());
    }
}
