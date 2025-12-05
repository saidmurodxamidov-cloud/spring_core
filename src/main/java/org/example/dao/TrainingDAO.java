package org.example.dao;


import org.example.model.TrainingDTO;

import java.util.List;

public interface TrainingDAO {
    void create(TrainingDTO trainingDTO);
    TrainingDTO findById(Long id);
    List<TrainingDTO> findAll();
}
