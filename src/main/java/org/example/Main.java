package org.example;

import org.example.config.AppConfig;
import org.example.model.Trainee;
import org.example.model.Trainer;
import org.example.model.Training;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Map;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(AppConfig.class);
        Map<Long, Trainee> traineeStorage = context.getBean("traineeStorage", Map.class);
        Map<Long, Trainer> trainerStorage = context.getBean("trainerStorage", Map.class);
        Map<Long, Training> trainingStorage = context.getBean("trainingStorage", Map.class);

        System.out.println("Trainings: " + trainingStorage);
        System.out.println("Trainers: " + trainerStorage);
        System.out.println("Trainees: " + traineeStorage);

        context.close();
    }
}