package org.example.storage;

import lombok.Getter;
import org.example.model.Trainer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Getter
public class TrainerStorage {
    Map<Long, Trainer> storage = new HashMap<>();
}
