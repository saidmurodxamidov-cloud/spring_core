package org.example;

import org.example.config.AppConfig;

import org.example.dao.TrainerDAO;
import org.example.facade.GymFacade;
import org.example.model.Trainer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


import org.example.storage.TraineeStorage;
import org.example.storage.TrainerStorage;
import org.example.storage.TrainingStorage;

public class Main {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(AppConfig.class);

        TraineeStorage traineeStorage = context.getBean(TraineeStorage.class);
        TrainingStorage trainingStorage = context.getBean(TrainingStorage.class);
        TrainerStorage trainerStorage = context.getBean(TrainerStorage.class);
        TrainerDAO trainerDAO = context.getBean(TrainerDAO.class);
        Trainer trainer = new Trainer(12L,"saidmurod","xamidov","saidxam","password",true,"bot");
        trainerDAO.create(trainer);
        GymFacade gymFacade = context.getBean(GymFacade.class);
        gymFacade.updateTrainer(trainer);
        gymFacade.getAllTrainings().forEach(System.out::println);
        context.close();
    }
}