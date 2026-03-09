package org.example.cucumber.component.steps;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.example.dto.request.TrainingAddRequest;
import org.example.mq.ActionType;
import org.example.mq.WorkloadSenderDelegate;
import org.example.persistence.entity.*;
import org.example.persistence.repository.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Step definitions for the {@code training.feature} component scenarios.
 *
 * <p>HTTP calls are made via {@link TestRestTemplate}. Authentication is
 * simulated by adding {@code X-Auth-Username} and {@code X-Auth-Roles} headers,
 * mirroring the behaviour of the API gateway in production.
 *
 * <p>The JMS delegate ({@link WorkloadSenderDelegate}) is a Mockito mock, so
 * no real ActiveMQ broker is needed in component tests.
 */
public class TrainingComponentSteps {

    /* ───── Spring beans ───────────────────────────────────────────── */

    @Autowired private TestRestTemplate restTemplate;
    @Autowired private ScenarioContext  scenarioContext;

    @Autowired private WorkloadSenderDelegate workloadSenderDelegate;

    @Autowired private TrainingRepository     trainingRepository;
    @Autowired private TrainerRepository      trainerRepository;
    @Autowired private TraineeRepository      traineeRepository;
    @Autowired private TrainingTypeRepository trainingTypeRepository;
    @Autowired private UserRepository         userRepository;
    @Autowired private JdbcTemplate           jdbcTemplate;

    @Autowired private BCryptPasswordEncoder  bcrypt;
    @Autowired private ObjectMapper           objectMapper;

    /* ───── current request auth ───────────────────────────────────── */

    private String currentAuthUsername;
    private String currentAuthRoles;

    /* ═══════════════════════════════════════════════════════════════
       HOOKS
    ═══════════════════════════════════════════════════════════════ */

    @Before
    public void resetState() {
        currentAuthUsername = null;
        currentAuthRoles    = null;
        // Reset mock interactions between scenarios
        Mockito.reset(workloadSenderDelegate);
    }

    /* ═══════════════════════════════════════════════════════════════
       DATABASE SETUP STEPS  (shared with AuthComponentSteps via @Given)
    ═══════════════════════════════════════════════════════════════ */

    @Given("the database is clean")
    public void theDatabaseIsClean() {
        trainingRepository.deleteAll();
        trainerRepository.deleteAll();
        // Clear join tables that can block deletes due to FK constraints (H2).
        jdbcTemplate.update("DELETE FROM trainers_trainee");
        jdbcTemplate.update("DELETE FROM user_entity_roles");
        traineeRepository.deleteAll();
        trainingTypeRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Given("the following training type exists:")
    public void theFollowingTrainingTypeExists(DataTable table) {
        table.asMaps().forEach(row -> {
            TrainingTypeEntity type = TrainingTypeEntity.builder()
                    .trainingTypeName(row.get("name"))
                    .build();
            trainingTypeRepository.save(type);
        });
    }

    @Given("the following trainer exists:")
    public void theFollowingTrainerExists(DataTable table) {
        table.asMaps().forEach(row -> {
            UserEntity user = buildUser(
                    row.get("username"), row.get("firstName"),
                    row.get("lastName"),  row.get("password"),
                    Role.TRAINER);

            TrainerEntity trainer = TrainerEntity.builder()
                    .user(user)
                    .trainees(new HashSet<>())
                    .trainings(new HashSet<>())
                    .specializations(new HashSet<>())
                    .build();
            trainerRepository.save(trainer);
        });
    }

    @Given("the following trainee exists:")
    public void theFollowingTraineeExists(DataTable table) {
        table.asMaps().forEach(row -> {
            UserEntity user = buildUser(
                    row.get("username"), row.get("firstName"),
                    row.get("lastName"),  row.get("password"),
                    Role.TRAINEE);

            TraineeEntity trainee = TraineeEntity.builder()
                    .user(user)
                    .trainers(new HashSet<>())
                    .trainings(new HashSet<>())
                    .build();
            traineeRepository.save(trainee);
        });
    }

    @Given("a training {string} exists for trainer {string} and trainee {string} of type {string} on {string} for {int} minutes")
    public void aTrainingExists(String name, String trainerUsername, String traineeUsername,
                                String typeName, String date, int durationMinutes) {

        TrainerEntity trainer = trainerRepository.findByUserUserName(trainerUsername)
                .orElseThrow(() -> new IllegalStateException("Trainer not found: " + trainerUsername));
        TraineeEntity trainee = traineeRepository.findByUserUserName(traineeUsername)
                .orElseThrow(() -> new IllegalStateException("Trainee not found: " + traineeUsername));
        TrainingTypeEntity type = trainingTypeRepository.findByTrainingTypeName(typeName)
                .orElseThrow(() -> new IllegalStateException("Training type not found: " + typeName));

        TrainingEntity training = TrainingEntity.builder()
                .trainingName(name)
                .date(LocalDate.parse(date))
                .trainingDuration(Duration.ofMinutes(durationMinutes))
                .trainingType(type)
                .trainer(trainer)
                .trainee(trainee)
                .build();
        training = trainingRepository.save(training);
        scenarioContext.setLastCreatedTrainingId(training.getId());
    }

    /* ═══════════════════════════════════════════════════════════════
       AUTH SETUP STEPS
    ═══════════════════════════════════════════════════════════════ */

    @Given("I am authenticated as {string} with roles {string}")
    public void iAmAuthenticatedAs(String username, String roles) {
        this.currentAuthUsername = username;
        this.currentAuthRoles    = roles;
    }

    /* ═══════════════════════════════════════════════════════════════
       HTTP ACTION STEPS
    ═══════════════════════════════════════════════════════════════ */

    @When("I send a POST request to {string} with body:")
    public void iSendPostRequest(String path, DataTable table) {
        TrainingAddRequest body = buildTrainingRequest(table.asMap(String.class, String.class));
        ResponseEntity<String> response = restTemplate.exchange(
                path,
                HttpMethod.POST,
                new HttpEntity<>(body, buildAuthHeaders()),
                String.class);
        scenarioContext.setLastResponse(response);
        extractTrainingId(response);
    }

    @When("I send a POST request to {string} without authentication with body:")
    public void iSendPostRequestWithoutAuthentication(String path, DataTable table) {
        TrainingAddRequest body = buildTrainingRequest(table.asMap(String.class, String.class));
        ResponseEntity<String> response = restTemplate.exchange(
                path,
                HttpMethod.POST,
                new HttpEntity<>(body, new HttpHeaders()),
                String.class);
        scenarioContext.setLastResponse(response);
    }

    @When("I send a DELETE request to {string}")
    public void iSendDeleteRequest(String path) {
        ResponseEntity<String> response = restTemplate.exchange(
                path,
                HttpMethod.DELETE,
                new HttpEntity<>(buildAuthHeaders()),
                String.class);
        scenarioContext.setLastResponse(response);
    }

    @When("I delete the last created training")
    public void iDeleteTheLastCreatedTraining() {
        Long id = scenarioContext.getLastCreatedTrainingId();
        assertThat(id).as("No training was created in this scenario").isNotNull();
        iSendDeleteRequest("/api/trainings/" + id);
    }

    /* ═══════════════════════════════════════════════════════════════
       ASSERTION STEPS
    ═══════════════════════════════════════════════════════════════ */

    @Then("the response status should be {int}")
    public void theResponseStatusShouldBe(int expectedStatus) {
        assertThat(scenarioContext.getLastResponse().getStatusCode().value())
                .as("Response status")
                .isEqualTo(expectedStatus);
    }

    @Then("the response status should be 4xx or 5xx")
    public void theResponseStatusShouldBeErrorRange() {
        int status = scenarioContext.getLastResponse().getStatusCode().value();
        assertThat(status).as("Expected an error status code (4xx or 5xx)").isGreaterThanOrEqualTo(400);
    }

    @Then("the response status should be 401 or 403")
    public void theResponseStatusShouldBeUnauthorizedOrForbidden() {
        int status = scenarioContext.getLastResponse().getStatusCode().value();
        assertThat(status).as("Expected 401 or 403").isIn(401, 403);
    }

    @And("the response body should contain field {string} with value {string}")
    public void theResponseBodyShouldContainField(String field, String expectedValue) throws Exception {
        String body = scenarioContext.getLastResponse().getBody();
        JsonNode json = objectMapper.readTree(body);
        assertThat(json.get(field).asText())
                .as("Field '%s' in response body", field)
                .isEqualTo(expectedValue);
    }

    @And("a workload ADD event should be dispatched for trainer {string}")
    public void aWorkloadAddEventShouldBeDispatched(String trainerUsername) {
        ArgumentCaptor<org.example.dto.request.TrainerWorkloadRequest> captor =
                ArgumentCaptor.forClass(org.example.dto.request.TrainerWorkloadRequest.class);
        verify(workloadSenderDelegate).sendWorkload(captor.capture(), anyString());

        org.example.dto.request.TrainerWorkloadRequest captured = captor.getValue();
        assertThat(captured.getActionType()).isEqualTo(ActionType.ADD);
        assertThat(captured.getUsername()).isEqualTo(trainerUsername);
    }

    @And("a workload DELETE event should be dispatched for trainer {string}")
    public void aWorkloadDeleteEventShouldBeDispatched(String trainerUsername) {
        ArgumentCaptor<org.example.dto.request.TrainerWorkloadRequest> captor =
                ArgumentCaptor.forClass(org.example.dto.request.TrainerWorkloadRequest.class);
        verify(workloadSenderDelegate).sendWorkload(captor.capture(), anyString());

        org.example.dto.request.TrainerWorkloadRequest captured = captor.getValue();
        assertThat(captured.getActionType()).isEqualTo(ActionType.DELETE);
        assertThat(captured.getUsername()).isEqualTo(trainerUsername);
    }

    @And("no workload event should be dispatched")
    public void noWorkloadEventShouldBeDispatched() {
        verify(workloadSenderDelegate, never()).sendWorkload(any(), anyString());
    }

    /* ═══════════════════════════════════════════════════════════════
       PRIVATE HELPERS
    ═══════════════════════════════════════════════════════════════ */

    private HttpHeaders buildAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (currentAuthUsername != null) {
            headers.set("X-Auth-Username", currentAuthUsername);
        }
        if (currentAuthRoles != null) {
            headers.set("X-Auth-Roles", currentAuthRoles);
        }
        return headers;
    }

    private TrainingAddRequest buildTrainingRequest(Map<String, String> row) {
        return TrainingAddRequest.builder()
                .trainingName(row.get("trainingName"))
                .traineeUsername(row.get("traineeUsername"))
                .trainerUsername(row.get("trainerUsername"))
                .trainingType(row.get("trainingType"))
                .trainingDate(LocalDate.parse(row.get("trainingDate")))
                .trainingDurationInMinutes(Integer.parseInt(row.get("trainingDurationInMinutes")))
                .build();
    }

    private UserEntity buildUser(String username, String firstName,
                                 String lastName, String password, Role role) {
        return UserEntity.builder()
                .userName(username)
                .firstName(firstName)
                .lastName(lastName)
                .passwordHash(bcrypt.encode(password))
                .isActive(true)
                .roles(new HashSet<>(Set.of(role)))
                .build();
    }

    private void extractTrainingId(ResponseEntity<String> response) {
        try {
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode json = objectMapper.readTree(response.getBody());
                if (json.has("id")) {
                    scenarioContext.setLastCreatedTrainingId(json.get("id").asLong());
                }
            }
        } catch (Exception ignored) {
            // not all successful responses contain a training id
        }
    }
}
