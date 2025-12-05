package org.example.config;

import org.example.model.TraineeDTO;
import org.example.model.TrainerDTO;
import org.example.model.TrainingDTO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class StorageConfig {

    @Bean
    public Map<Long, TraineeDTO> traineeStorage() {
        return new HashMap<>();
    }

    @Bean
    public Map<Long, TrainerDTO> trainerStorage() {
        return new HashMap<>();
    }

    @Bean
    public Map<Long, TrainingDTO> trainingStorage(){
        return new HashMap<>();
    }
}
