package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dao.TraineeDAO;
import org.example.model.Trainee;
import org.example.model.User;
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

    public void createTrainee(Trainee trainee) {

        Set<String> existingUsernames = traineeDAO.findAll().stream()
                .map(User::getUserName)
                .collect(Collectors.toSet());
        String username = UsernameGenerator.generateUsername(trainee.getFirstName(), trainee.getLastName(), existingUsernames);
        trainee.setUserName(username);

        String password = PasswordGenerator.generatePassword();
        trainee.setPassword(password);

        log.debug("Creating trainee: {}", trainee.getUserName());
        traineeDAO.create(trainee);
        log.info("Trainee created with ID: {}", trainee.getUserId());
    }

    public void updateTrainee(Trainee trainee) {
        log.debug("Updating trainee with ID: {}", trainee.getUserId());
        traineeDAO.update(trainee);
        log.info("Trainee updated successfully with id: {}", trainee.getUserId());
    }

    public void deleteTrainee(Long id) {
        log.debug("Deleting trainee with ID: {}", id);
        traineeDAO.delete(id);
        log.info("Trainee deleted successfully with id: {}" ,id);
    }

    public Trainee getTraineeById(Long id) {
        log.debug("Fetching trainee with ID: {}", id);
        return traineeDAO.findById(id);
    }

    public List<Trainee> getAllTrainees() {
        log.debug("Fetching all trainees");
        return traineeDAO.findAll();
    }
}
