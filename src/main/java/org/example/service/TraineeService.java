package org.example.service;

import lombok.extern.slf4j.Slf4j;
import org.example.dao.TraineeDAO;
import org.example.model.TraineeDTO;
import org.example.model.UserDTO;
import org.example.util.PasswordGenerator;
import org.example.util.UsernameGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TraineeService {

    private TraineeDAO traineeDAO;

    @Autowired
    public void setTraineeDAO(TraineeDAO traineeDAO) {
        this.traineeDAO = traineeDAO;
    }

    public void createTrainee(TraineeDTO traineeDTO) {

        Set<String> existingUsernames = traineeDAO.findAll().stream()
                .map(UserDTO::getUserName)
                .collect(Collectors.toSet());
        String username = UsernameGenerator.generateUsername(traineeDTO.getFirstName(), traineeDTO.getLastName(), existingUsernames);
        traineeDTO.setUserName(username);

        char[] password = PasswordGenerator.generatePassword();
        traineeDTO.setPassword(password);

        log.debug("Creating trainee: {}", traineeDTO.getUserName());
        traineeDAO.create(traineeDTO);
        log.info("Trainee created with ID: {}", traineeDTO.getUserId());
    }

    public void updateTrainee(TraineeDTO traineeDTO) {
        log.debug("Updating trainee with ID: {}", traineeDTO.getUserId());
        traineeDAO.update(traineeDTO);
        log.info("Trainee updated successfully with id: {}", traineeDTO.getUserId());
    }

    public void deleteTrainee(Long id) {
        log.debug("Deleting trainee with ID: {}", id);
        traineeDAO.delete(id);
        log.info("Trainee deleted successfully with id: {}" ,id);
    }

    public TraineeDTO getTraineeById(Long id) {
        log.debug("Fetching trainee with ID: {}", id);
        return traineeDAO.findById(id);
    }

    public List<TraineeDTO> getAllTrainees() {
        log.debug("Fetching all trainees");
        return traineeDAO.findAll();
    }
}
