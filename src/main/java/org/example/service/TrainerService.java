package org.example.service;

import lombok.extern.slf4j.Slf4j;
import org.example.dao.TrainerDAO;
import org.example.model.TrainerDTO;
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

    public void createTrainer(TrainerDTO trainerDTO) {
        log.debug("Creating trainer: {}", trainerDTO.getUserName());
        trainerDAO.create(trainerDTO);
        log.info("Trainer created with ID: {}", trainerDTO.getUserId());
    }

    public void updateTrainer(TrainerDTO trainerDTO) {
        log.debug("Updating trainer with ID: {}", trainerDTO.getUserId());
        trainerDAO.update(trainerDTO);
        log.info("Trainer updated successfully with id: {}", trainerDTO.getUserName());
    }

    public TrainerDTO getTrainerById(Long id) {
        log.debug("Fetching trainer with ID: {}", id);
        return trainerDAO.findById(id);
    }

    public List<TrainerDTO> getAllTrainers() {
        log.debug("Fetching all trainers");
        return trainerDAO.findAll();
    }
}
