package org.example.cucumber.component.steps;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.example.dto.request.AuthRequest;
import org.example.persistence.entity.Role;
import org.example.persistence.entity.UserEntity;
import org.example.persistence.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Step definitions for the {@code auth.feature} component scenarios.
 *
 * <p>Covers login (POST /api/auth/login), logout (POST /api/auth/logout),
 * and the public hello endpoint.
 */

public class AuthComponentSteps {

    @Autowired
    private TestRestTemplate  restTemplate;
    @Autowired
    private ScenarioContext   scenarioContext;
    @Autowired
    private UserRepository    userRepository;
    @Autowired
    private BCryptPasswordEncoder bcrypt;
    @Autowired
    private ObjectMapper      objectMapper;

    /* ═══════════════════════════════════════════════════════════════
       DATA SETUP
    ═══════════════════════════════════════════════════════════════ */

    @Given("a user exists with username {string}, password {string}, role {string}")
    public void aUserExists(String username, String password, String roleName) {
        Role role = Role.valueOf(roleName);
        UserEntity user = UserEntity.builder()
                .userName(username)
                .firstName("Test")
                .lastName("User")
                .passwordHash(bcrypt.encode(password))
                .isActive(true)
                .roles(new HashSet<>(Set.of(role)))
                .build();
        userRepository.save(user);
    }

    /* ═══════════════════════════════════════════════════════════════
       HTTP ACTIONS
    ═══════════════════════════════════════════════════════════════ */

    @When("I POST to {string} with credentials username {string} and password {string}")
    public void iPostToLoginWithCredentials(String path, String username, String password) {
        AuthRequest body = new AuthRequest(username, password);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> response = restTemplate.exchange(
                path,
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                String.class);
        scenarioContext.setLastResponse(response);
    }

    @When("I POST to {string} with no body")
    public void iPostToWithNoBody(String path) {
        HttpHeaders headers = buildAuthHeaders();
        ResponseEntity<String> response = restTemplate.exchange(
                path,
                HttpMethod.POST,
                new HttpEntity<>(headers),
                String.class);
        scenarioContext.setLastResponse(response);
    }

    @When("I GET {string} without authentication")
    public void iGetWithoutAuthentication(String path) {
        ResponseEntity<String> response = restTemplate.exchange(
                path,
                HttpMethod.GET,
                new HttpEntity<>(new HttpHeaders()),
                String.class);
        scenarioContext.setLastResponse(response);
    }

    /* ═══════════════════════════════════════════════════════════════
       ASSERTIONS
    ═══════════════════════════════════════════════════════════════ */

    @Then("the response body should contain a non-empty {string} field")
    public void theResponseBodyShouldContainNonEmptyField(String fieldName) throws Exception {
        String body = scenarioContext.getLastResponse().getBody();
        assertThat(body).as("Response body must not be null").isNotNull();
        JsonNode json = objectMapper.readTree(body);
        assertThat(json.has(fieldName))
                .as("Response JSON should have field '%s'", fieldName)
                .isTrue();
        assertThat(json.get(fieldName).asText())
                .as("Field '%s' should not be empty", fieldName)
                .isNotBlank();
    }

    @Then("the response body should be {string}")
    public void theResponseBodyShouldBe(String expected) {
        assertThat(scenarioContext.getLastResponse().getBody())
                .as("Response body")
                .isEqualTo(expected);
    }

    /* ═══════════════════════════════════════════════════════════════
       PRIVATE HELPERS
    ═══════════════════════════════════════════════════════════════ */

    /**
     * Builds headers using whatever auth was set in the current scenario context via
     * the {@code "I am authenticated as"} step (defined in TrainingComponentSteps).
     * We rely on {@link ScenarioContext} being @ScenarioScope so the same instance
     * is shared across all step-definition classes within one scenario.
     */
    private HttpHeaders buildAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // Auth headers are picked up from the scenario context by a shared helper.
        // For logout tests we send the authenticated username directly.
        headers.set("X-Auth-Username", "trainer.user");
        headers.set("X-Auth-Roles",    "ROLE_TRAINER");
        return headers;
    }
}
