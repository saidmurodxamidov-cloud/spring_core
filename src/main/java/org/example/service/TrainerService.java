package org.example.service;

import lombok.extern.slf4j.Slf4j;
import org.example.dao.TrainerDAO;
import org.example.model.Trainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class TrainerService {

    private TrainerDAO trainerDAO;

    @Autowired
    public void setTrainerDAO(TrainerDAO trainerDAO) {
        this.trainerDAO = trainerDAO;
    }

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
