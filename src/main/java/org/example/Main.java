package org.example;

import org.example.config.AppConfig;

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
        System.out.println(trainingStorage.getStorage());
        System.out.println(trainerStorage.getStorage());
        System.out.println(traineeStorage.getStorage());

        context.close();
    }
}