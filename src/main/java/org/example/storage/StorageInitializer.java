package org.example.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
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

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class StorageInitializer {

    private final Map<Long, Trainee> traineeStorage;
    private final Map<Long, Trainer> trainerStorage;
    private final Map<Long, Training> trainingStorage;
    private final ObjectMapper mapper;

    @Value("${storage.init.path}")
    private Resource dataFile;

    @PostConstruct
    public void init() {
        try {
            DataWrapper wrapper = mapper.readValue(dataFile.getInputStream(), DataWrapper.class);
            wrapper.getTrainees().forEach(t -> traineeStorage.put(t.getUserId(), t));
            wrapper.getTrainers().forEach(t -> trainerStorage.put(t.getUserId(), t));
            wrapper.getTrainings().forEach(t -> trainingStorage.put(t.getTrainingId(), t));

            log.info("In-memory storages initialized successfully from {}", dataFile.getFilename());
        } catch (Exception e) {
            log.error("Failed to initialize storage: {}", e.getMessage(), e);
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
