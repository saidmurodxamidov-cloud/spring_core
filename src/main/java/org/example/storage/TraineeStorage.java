package org.example.storage;

import lombok.Getter;
import org.example.model.Trainee;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Getter
@Component
public class TraineeStorage {
    private final Map<Long, Trainee> storage = new HashMap<>();

}
