package org.example.dao;

import lombok.RequiredArgsConstructor;
import org.example.exception.EntityAlreadyExistException;
import org.example.exception.EntityNotFoundException;
import org.example.model.Trainee;
import org.example.storage.TraineeStorage;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TraineeDaoImp implements TraineeDAO{

    private final TraineeStorage traineeStorage;


    @Override
    public void create(Trainee trainee) {
        if (traineeStorage.getStorage().containsKey(trainee.getUserId()))
            throw new EntityAlreadyExistException();

        traineeStorage.getStorage().put(trainee.getUserId(),trainee);
    }

    @Override
    public void update(Trainee trainee) {
        if (!traineeStorage.getStorage().containsKey(trainee.getUserId()))
            throw new EntityNotFoundException();
        traineeStorage.getStorage().put(trainee.getUserId(), trainee);

    }

    @Override
    public void delete(Long id) {
        if(!traineeStorage.getStorage().containsKey(id))
            throw new EntityNotFoundException();
        traineeStorage.getStorage().remove(id);
    }

    @Override
    public Trainee findById(Long id) {
        if(!traineeStorage.getStorage().containsKey(id))
            throw new EntityNotFoundException();
        return traineeStorage.getStorage().get(id);
    }

    @Override
    public List<Trainee> findAll() {
        return traineeStorage.getStorage().values().stream().toList();
    }
}
