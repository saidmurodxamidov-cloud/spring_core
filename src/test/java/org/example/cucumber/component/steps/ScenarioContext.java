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

    /** The HTTP response captured by the most recent "When" step. */
    private ResponseEntity<String> lastResponse;

    /** ID of the last training that was created, used by delete steps. */
    private Long lastCreatedTrainingId;

    /** Tracks whether an exception was thrown during the last consumer invocation. */
    private Exception lastException;
}
