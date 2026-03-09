package org.example.cucumber.component.steps;

import io.cucumber.spring.ScenarioScope;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * Scenario-scoped Spring bean that carries HTTP response state between step definitions.
 * A fresh instance is created for every Cucumber scenario, so there is no leakage
 * between scenarios.
 */
@Component
@ScenarioScope
@Getter
@Setter
public class ScenarioContext {

  
    private ResponseEntity<String> lastResponse;

    
    private Long lastCreatedTrainingId;

    private Exception lastException;
}
