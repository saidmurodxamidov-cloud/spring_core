package org.example.dao;

import org.example.model.Trainee;

import java.util.List;

public interface TraineeDAO {
    void create(Trainee trainee);
    void update(Trainee trainee);
    void delete(Long id);
    Trainee findById(Long id);
    List<Trainee> findAll();
}
