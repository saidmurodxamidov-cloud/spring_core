package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dao.TrainerDAO;
import org.example.model.Trainer;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainerService {

    private final TrainerDAO trainerDAO;

    public void createTrainer(Trainer trainer) {
        log.debug("Creating trainer: {}", trainer.getUserName());
        trainerDAO.create(trainer);
        log.info("Trainer created with ID: {}", trainer.getUserId());
    }

    public void updateTrainer(Trainer trainer) {
        log.debug("Updating trainer with ID: {}", trainer.getUserId());
        trainerDAO.update(trainer);
        log.info("Trainer updated successfully.");
    }

    public Trainer getTrainerById(Long id) {
        log.debug("Fetching trainer with ID: {}", id);
        return trainerDAO.findById(id);
    }

    public List<Trainer> getAllTrainers() {
        log.debug("Fetching all trainers");
        return trainerDAO.findAll();
    }
}
