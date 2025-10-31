package org.example.dao;


import org.example.model.Training;

import java.util.List;

public interface TrainingDAO {
    void create(Training training);
    void update(Training training);
    void delete(Training training);
    Training findById(Long id);



    List<Training> findAll();
}
