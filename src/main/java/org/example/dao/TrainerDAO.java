package org.example.dao;

import org.example.model.TrainerDTO;

import java.util.List;

public interface TrainerDAO {
    void create(TrainerDTO trainee);
    void update(TrainerDTO trainee);
    TrainerDTO findById(Long id);
    List<TrainerDTO> findAll();
}
