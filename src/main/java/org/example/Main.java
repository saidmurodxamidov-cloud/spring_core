package org.example;

import org.example.config.AppConfig;
import org.example.dao.TrainingDAO;
import org.example.enums.TrainingType;
import org.example.model.Training;
import org.example.storage.InMemoryStorage;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.LocalDate;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(AppConfig.class);
        var bean  = context.getBean(InMemoryStorage.class);
        System.out.println(bean.getTraineeStorage());
        System.out.println(bean.getTrainerStorage());
        System.out.println(bean.getTraineeStorage());
        var traningDao = context.getBean(TrainingDAO.class);
        traningDao.create(new Training(12L,23L, LocalDate.now(),"saidmurod", TrainingType.YOGA,90));
        System.out.println(traningDao.findAll());
        context.close();
    }
}