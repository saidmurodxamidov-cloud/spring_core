package org.example.storage;

import lombok.Getter;
import org.example.model.Trainee;
import org.example.model.Trainer;
import org.example.model.Training;
import org.springframework.stereotype.Component;

import java.util.*;

@Getter
@Component
public class InMemoryStorage {
    private final Map<Long, Trainee> traineeStorage = new HashMap<>();
    private final Map<Long, Trainer> trainerStorage = new HashMap<>();
    private final Set<Training> trainingStorage = new HashSet<>();

}
