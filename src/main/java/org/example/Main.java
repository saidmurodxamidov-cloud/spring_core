package org.example;

import lombok.extern.slf4j.Slf4j;
import org.example.config.*;
import org.example.mapper.TrainingMapper;
import org.example.persistence.model.*;
import org.example.service.AuthService;
import org.example.service.*;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Set;

@Slf4j
public class Main {

    public static void main(String[] args) {
        log.debug("Starting application...");

        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(
                             AppConfig.class
                     )) {

            log.info("Spring context initialized successfully.");
            TrainingTypeService trainingTypeService =
                    context.getBean(TrainingTypeService.class);

            TrainerService trainerService =
                    context.getBean(TrainerService.class);

            TraineeService traineeService =
                    context.getBean(TraineeService.class);

            TrainingService trainingService =
                    context.getBean(TrainingService.class);

            TrainingMapper trainingMapper = context.getBean(TrainingMapper.class);
            UserService userService =
                    context.getBean(UserService.class);

            AuthService authService = context.getBean(AuthService.class);

            authService.login("alex.jipacha4","trainerPass");

            TrainingTypeDTO trainingTypeDTO = new TrainingTypeDTO();
            trainingTypeDTO.setTrainingTypeName("YOGA");
            UserDTO user = new UserDTO(
                    1L,
                    "John",
                    "Doe",
                    "johndoe",
                    "password123".toCharArray(),
                    true
            );

            // ---------- TraineeDTO ----------
            TraineeDTO trainee = new TraineeDTO(
                    2L,
                    "saidmurod",
                    "xamidov",
                    "alicesmith",
                    "traineePass".toCharArray(),
                    true,
                    LocalDate.of(1998, 5, 20),
                    "Tashkent, Uzbekistan"
            );

            // ---------- TrainingTypeDTO ----------
            TrainingTypeDTO yogaType = new TrainingTypeDTO(
                    1L,
                    "YOGA"
            );

            TrainingTypeDTO strengthType = new TrainingTypeDTO(
                    2L,
                    "STRENGTH"
            );

            // ---------- TrainerDTO ----------
            TrainerDTO trainer = new TrainerDTO();
            trainer.setUserId(3L);
            trainer.setFirstName("alex");
            trainer.setLastName("jipacha");
            trainer.setUserName("polatcha");
            trainer.setPassword("trainerPass".toCharArray());
            trainer.setActive(true);
            trainer.setSpecialization(Set.of(yogaType, strengthType));

//            strengthType = trainingTypeService.create(strengthType);
//            yogaType = trainingTypeService.create(yogaType);
//            trainee = traineeService.createTrainee(trainee);
//            trainer = trainerService.createTrainer(trainer);
            System.out.println(strengthType);
            System.out.println(trainee);
            System.out.println(trainer);
            System.out.println(yogaType);


            // ---------- TrainingDTO ----------
            TrainingDTO training = new TrainingDTO(
                    2L,
                    trainee.getUserId(),
                    trainer.getUserId(),
                    LocalDate.now(),
                    "Morning Yoga Session",
                    yogaType,
                    Duration.ofMinutes(60)
            );

//            training = trainingService.createTraining(training);
            System.out.println(training);
//            System.out.println(trainingService.getAllTraineeTrainings(trainee.getUserName()));

//            System.out.println(trainerService.getTrainersNotAssignedToTrainee(trainee.getUserName()));


            System.out.println(SecurityContextHolder.getContext().getAuthentication());
        } catch (Exception e) {
            log.error("Application failed to start", e);
            System.exit(1);
        }

        log.info("Application shutdown complete.");
    }
}