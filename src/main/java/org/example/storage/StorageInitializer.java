package org.example.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Getter;
import lombok.Setter;
import org.example.model.Trainee;
import org.example.model.Trainer;
import org.example.model.Training;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import jakarta.annotation.PostConstruct;


@Component
public class StorageInitializer {

    private final InMemoryStorage storage;

    @Value("${storage.init.path}")
    private String dataFilePath;

    public StorageInitializer(InMemoryStorage storage) {
        this.storage = storage;
    }

    @PostConstruct
    public void init() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            DataWrapper wrapper = mapper.readValue(new File(dataFilePath), DataWrapper.class);
            wrapper.getTrainees().forEach(t -> storage.getTraineeStorage().put(t.getUserId(), t));
            wrapper.getTrainers().forEach(t -> storage.getTrainerStorage().put(t.getUserId(), t));

            storage.getTrainingStorage().addAll(wrapper.getTrainings());

            System.out.println("In-memory storage initialized successfully from JSON");
        } catch (Exception e) {
            System.err.println("Failed to initialize storage: " + e.getMessage());
        }
    }

    // Wrapper for JSON data
    @Setter
    @Getter
    public static class DataWrapper {
        private List<Trainee> trainees;
        private List<Trainer> trainers;
        private List<Training> trainings;

    }
}
