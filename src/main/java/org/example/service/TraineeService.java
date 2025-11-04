package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dao.TraineeDAO;
import org.example.model.Trainee;
import org.example.util.PasswordGenerator;
import org.example.util.UsernameGenerator;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TraineeService {

    private final TraineeDAO traineeDAO;

    public void createTrainee(Trainee trainee) {
        // Generate unique username
        Set<String> existingUsernames = traineeDAO.findAll().stream()
                .map(t -> t.getUserName())
                .collect(Collectors.toSet());
        String username = UsernameGenerator.generateUsername(trainee.getFirstName(), trainee.getLastName(), existingUsernames);
        trainee.setUserName(username);

        // Generate random password
        String password = PasswordGenerator.generatePassword();
        trainee.setPassword(password);

        log.info("Creating trainee: {} with password: {}", trainee.getUserName(), trainee.getPassword());
        traineeDAO.create(trainee);
        log.info("Trainee created with ID: {}", trainee.getUserId());
    }

    public void updateTrainee(Trainee trainee) {
        log.info("Updating trainee with ID: {}", trainee.getUserId());
        traineeDAO.update(trainee);
        log.info("Trainee updated successfully.");
    }

    public void deleteTrainee(Long id) {
        log.info("Deleting trainee with ID: {}", id);
        traineeDAO.delete(id);
        log.info("Trainee deleted successfully.");
    }

    public Trainee getTraineeById(Long id) {
        log.info("Fetching trainee with ID: {}", id);
        return traineeDAO.findById(id);
    }

    public List<Trainee> getAllTrainees() {
        log.info("Fetching all trainees");
        return traineeDAO.findAll();
    }
}
