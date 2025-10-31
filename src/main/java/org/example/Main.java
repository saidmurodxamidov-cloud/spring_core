package org.example;

import org.example.config.AppConfig;
import org.example.storage.InMemoryStorage;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

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
        context.close();
    }
}