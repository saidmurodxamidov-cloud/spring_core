package org.example;

import org.example.config.AppConfig;


import org.example.dao.TrainerDAO;
import org.example.facade.GymFacade;
import org.example.model.Trainee;
import org.example.model.Trainer;
import org.example.model.Training;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Map;

public class Main {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(AppConfig.class);

        Map<Long, Trainee> traineeStorage = context.getBean("traineeStorage", Map.class);
        Map<Long, Trainer> trainerStorage = context.getBean("trainerStorage", Map.class);
        Map<Long, Training> trainingStorage = context.getBean("trainingStorage", Map.class);
        TrainerDAO trainerDAO = context.getBean(TrainerDAO.class);
        Trainer trainer = new Trainer(12L,"saidmurod","xamidov","saidxam","password",true,"bot");
        trainerDAO.create(trainer);
        GymFacade gymFacade = context.getBean(GymFacade.class);
        gymFacade.updateTrainer(trainer);
        gymFacade.getAllTrainings().forEach(System.out::println);

        System.out.println("Trainings: " + trainingStorage);
        System.out.println("Trainers: " + trainerStorage);
        System.out.println("Trainees: " + traineeStorage);


        context.close();
    }
}