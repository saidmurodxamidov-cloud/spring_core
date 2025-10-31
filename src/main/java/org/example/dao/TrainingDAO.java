package org.example.dao;


import org.example.model.Training;

import java.util.List;

public interface TrainingDAO {
    void create(Training training);
    List<Training> findAll();
}
