package org.example.dao;

import org.example.model.TraineeDTO;

import java.util.List;

public interface TraineeDAO {
    void create(TraineeDTO traineeDTO);
    void update(TraineeDTO traineeDTO);
    void delete(Long id);
    TraineeDTO findById(Long id);
    List<TraineeDTO> findAll();
}
