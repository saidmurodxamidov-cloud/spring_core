package org.example.dao;

import org.example.model.Trainer;

import java.util.List;

public interface TrainerDAO {
    void create(Trainer trainee);
    void update(Trainer trainee);
    Trainer findById(Long id);
    List<Trainer> findAll();
}
