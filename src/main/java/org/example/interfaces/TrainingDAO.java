package org.example.interfaces;


import org.example.model.Training;

import java.util.List;

public interface TrainingDAO {
    void create(Training training);
    Training findById(Long id);
    List<Training> findAll();
}
