package org.example;

import org.example.config.DataSourceConfig;
import org.example.config.EntityManagerConfig;
import org.example.config.RepositoryConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args) {
        // 1️⃣ Create Spring context
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

        // 2️⃣ Register your configuration classes
        context.register(
                DataSourceConfig.class,
                EntityManagerConfig.class,
                RepositoryConfig.class
        );

        // 3️⃣ Refresh context to initialize beans
        context.refresh();

        System.out.println("Spring context initialized successfully.");



        // 5️⃣ Close context when done
        context.close();
    }
}
