package org.example;

import lombok.extern.slf4j.Slf4j;
import org.example.config.*;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@Slf4j
public class Main {

    public static void main(String[] args) {
        log.debug("Starting application...");

        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(
                             AppConfig.class
                     )) {

            log.info("Spring context initialized successfully.");

        } catch (Exception e) {
            log.error("Application failed to start", e);
            System.exit(1);
        }

        log.info("Application shutdown complete.");
    }
}