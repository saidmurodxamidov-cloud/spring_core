package org.example.storage;

import lombok.Getter;
import org.example.model.Training;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Getter
@Component
public class TrainingStorage {
    private final Map<Long, Training> storage = new HashMap<>();

}
