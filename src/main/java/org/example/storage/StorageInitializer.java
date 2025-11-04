package org.example.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.example.model.Trainee;
import org.example.model.Trainer;
import org.example.model.Training;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import jakarta.annotation.PostConstruct;


@Component
@RequiredArgsConstructor
@Slf4j
public class StorageInitializer {

    private final TraineeStorage traineeStorage;
    private final TrainerStorage trainerStorage;
    private final TrainingStorage trainingStorage;
    private final ObjectMapper mapper;

    @Value("${storage.init.path}")
    private Resource dataFile;


    @PostConstruct
    public void init() {
        try {
//            log.info(dataFile.getInputStream().toString();
            DataWrapper wrapper = mapper.readValue(dataFile.getInputStream(), DataWrapper.class);
            wrapper.getTrainees().forEach(t -> traineeStorage.getStorage().put(t.getUserId(), t));
            wrapper.getTrainers().forEach(t -> trainerStorage.getStorage().put(t.getUserId(), t));
            wrapper.getTrainings().forEach(t -> trainingStorage.getStorage().put(t.getTrainingId(),t));

            log.info("in memory storages are initialized successfully!");
        } catch (Exception e) {
            log.error("Failed to initialize storage: {}", e.getMessage());
        }
    }

    @Getter
    @Setter
    public static class DataWrapper {
        private List<Trainee> trainees;
        private List<Trainer> trainers;
        private List<Training> trainings;

    }

}
