package org.example.cucumber.integration.steps;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.example.dto.request.TrainingAddRequest;
import org.example.cucumber.integration.config.IntegrationTestConfig;
import org.example.cucumber.integration.config.IntegrationTestConfig.CapturedMessage;
import org.example.mq.ActionType;
import org.example.persistence.entity.*;
import org.example.persistence.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Step definitions for integration scenarios that verify the full message flow:
 *
 * <pre>
 *   Training REST API  →  TrainingService  →  WorkloadSender  →  ActiveMQ queue
 *                                                                        ↓
 *                                                            TestWorkloadListener
 * </pre>
 */
public class TrainingWorkloadIntegrationSteps {

    /* ───── Spring beans ───────────────────────────────────────────── */

    @Autowired private TestRestTemplate  restTemplate;
    @Autowired private BCryptPasswordEncoder bcrypt;

    @Autowired private TrainingRepository     trainingRepository;
    @Autowired private TrainerRepository      trainerRepository;
    @Autowired private TraineeRepository      traineeRepository;
    @Autowired private TrainingTypeRepository trainingTypeRepository;
    @Autowired private UserRepository         userRepository;
    @Autowired private JdbcTemplate           jdbcTemplate;

    @Autowired private IntegrationTestConfig.TestWorkloadListener workloadListener;

    /* ───── scenario state ─────────────────────────────────────────── */

    private String currentAuthUsername;
    private String currentAuthRoles;

    private ResponseEntity<String> lastResponse;
    private Long lastCreatedTrainingId;
    private CapturedMessage capturedMessage;

    /* ═══════════════════════════════════════════════════════════════
       HOOKS
    ═══════════════════════════════════════════════════════════════ */

    @Before
    public void resetIntegrationState() {
        currentAuthUsername  = null;
        currentAuthRoles     = null;
        lastResponse         = null;
        lastCreatedTrainingId = null;
        capturedMessage      = null;
        workloadListener.clearMessages();
    }

    /* ═══════════════════════════════════════════════════════════════
       DATA SETUP STEPS
    ═══════════════════════════════════════════════════════════════ */

    @Given("the integration database is clean")
    public void theIntegrationDatabaseIsClean() {
        trainingRepository.deleteAll();
        trainerRepository.deleteAll();
        jdbcTemplate.update("DELETE FROM trainers_trainee");
        jdbcTemplate.update("DELETE FROM user_entity_roles");
        traineeRepository.deleteAll();
        trainingTypeRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Given("the following training type exists for integration:")
    public void integrationTrainingTypeExists(DataTable table) {
        table.asMaps().forEach(row -> trainingTypeRepository.save(
                TrainingTypeEntity.builder().trainingTypeName(row.get("name")).build()));
    }

    @Given("the following trainer exists for integration:")
    public void integrationTrainerExists(DataTable table) {
        table.asMaps().forEach(row -> {
            UserEntity user = buildUser(row.get("username"), row.get("firstName"),
                    row.get("lastName"), "defaultPass", Role.TRAINER);
            trainerRepository.save(TrainerEntity.builder()
                    .user(user)
                    .trainees(new HashSet<>())
                    .trainings(new HashSet<>())
                    .specializations(new HashSet<>())
                    .build());
        });
    }

    @Given("the following trainee exists for integration:")
    public void integrationTraineeExists(DataTable table) {
        table.asMaps().forEach(row -> {
            UserEntity user = buildUser(row.get("username"), row.get("firstName"),
                    row.get("lastName"), "defaultPass", Role.TRAINEE);
            traineeRepository.save(TraineeEntity.builder()
                    .user(user)
                    .trainers(new HashSet<>())
                    .trainings(new HashSet<>())
                    .build());
        });
    }

    @Given("I am authenticated as {string} with roles {string}")
    public void iAmAuthenticatedAs(String username, String roles) {
        this.currentAuthUsername = username;
        this.currentAuthRoles    = roles;
    }

    @Given("a training {string} exists for integration with trainer {string} and trainee {string}")
    public void aTrainingExistsForIntegration(String name, String trainerUser, String traineeUser) {
        TrainerEntity trainer = trainerRepository.findByUserUserName(trainerUser)
                .orElseThrow(() -> new IllegalStateException("Trainer not found: " + trainerUser));
        TraineeEntity trainee = traineeRepository.findByUserUserName(traineeUser)
                .orElseThrow(() -> new IllegalStateException("Trainee not found: " + traineeUser));
        TrainingTypeEntity type = trainingTypeRepository.findAll().get(0);

        TrainingEntity training = TrainingEntity.builder()
                .trainingName(name)
                .date(LocalDate.now())
                .trainingDuration(Duration.ofMinutes(60))
                .trainingType(type)
                .trainer(trainer)
                .trainee(trainee)
                .build();
        training = trainingRepository.save(training);
        lastCreatedTrainingId = training.getId();
    }

    /* ═══════════════════════════════════════════════════════════════
       HTTP ACTION STEPS
    ═══════════════════════════════════════════════════════════════ */

    @When("I send an integration POST request to {string} with body:")
    public void iSendIntegrationPostRequest(String path, DataTable table) {
        Map<String, String> row = table.asMap(String.class, String.class);
        TrainingAddRequest body = TrainingAddRequest.builder()
                .trainingName(row.get("trainingName"))
                .traineeUsername(row.get("traineeUsername"))
                .trainerUsername(row.get("trainerUsername"))
                .trainingType(row.get("trainingType"))
                .trainingDate(LocalDate.parse(row.get("trainingDate")))
                .trainingDurationInMinutes(Integer.parseInt(row.get("trainingDurationInMinutes")))
                .build();

        lastResponse = restTemplate.exchange(
                path, HttpMethod.POST,
                new HttpEntity<>(body, buildAuthHeaders()),
                String.class);

        extractTrainingIdFromResponse();
    }

    @When("I send an integration DELETE request for the last created training")
    public void iSendIntegrationDeleteRequestForLastTraining() {
        assertThat(lastCreatedTrainingId).as("No training was created").isNotNull();
        lastResponse = restTemplate.exchange(
                "/api/trainings/" + lastCreatedTrainingId,
                HttpMethod.DELETE,
                new HttpEntity<>(buildAuthHeaders()),
                String.class);
    }

    @When("I send a DELETE request to {string} for integration")
    public void iSendDeleteRequestForIntegration(String path) {
        lastResponse = restTemplate.exchange(
                path, HttpMethod.DELETE,
                new HttpEntity<>(buildAuthHeaders()),
                String.class);
    }

    /* ═══════════════════════════════════════════════════════════════
       ASSERTION STEPS
    ═══════════════════════════════════════════════════════════════ */

    @Then("the training creation response status should be {int}")
    public void trainingCreationResponseShouldBe(int status) {
        assertThat(lastResponse.getStatusCode().value()).isEqualTo(status);
    }

    @Then("the training creation response status should be 4xx or 5xx")
    public void trainingCreationResponseShouldBeError() {
        assertThat(lastResponse.getStatusCode().value()).isGreaterThanOrEqualTo(400);
    }

    @Then("the delete training response status should be {int}")
    public void deleteTrainingResponseShouldBe(int status) {
        assertThat(lastResponse.getStatusCode().value()).isEqualTo(status);
    }

    @Then("within {int} seconds a message should arrive on the workload queue")
    public void withinSecondsAMessageShouldArrive(int timeoutSeconds) throws InterruptedException {
        capturedMessage = workloadListener.pollMessage(timeoutSeconds);
        assertThat(capturedMessage)
                .as("Expected a workload message within %d seconds but none arrived", timeoutSeconds)
                .isNotNull();
    }

    @And("the received workload message should have action {string}")
    public void theReceivedMessageShouldHaveAction(String action) {
        assertThat(capturedMessage).as("No message was captured").isNotNull();
        assertThat(capturedMessage.request().getActionType())
                .isEqualTo(ActionType.valueOf(action));
    }

    @And("the received workload message should have trainerUsername {string}")
    public void theReceivedMessageShouldHaveTrainerUsername(String username) {
        assertThat(capturedMessage.request().getUsername()).isEqualTo(username);
    }

    @And("the received workload message should have duration {int} minutes")
    public void theReceivedMessageShouldHaveDuration(int expectedMinutes) {
        assertThat(capturedMessage.request().getDuration()).isEqualTo(expectedMinutes);
    }

    @And("the received workload message should have a non-empty idempotency key header")
    public void theReceivedMessageShouldHaveIdempotencyKey() {
        assertThat(capturedMessage.idempotencyKey())
                .as("Idempotency key should be present in JMS message headers")
                .isNotBlank();
    }

    @Then("no workload message should arrive on the queue within {int} seconds")
    public void noWorkloadMessageShouldArriveWithin(int timeoutSeconds) throws InterruptedException {
        boolean noMessage = workloadListener.noMessageWithin(timeoutSeconds);
        assertThat(noMessage)
                .as("Expected no workload message within %d seconds, but one arrived", timeoutSeconds)
                .isTrue();
    }

    /* ═══════════════════════════════════════════════════════════════
       PRIVATE HELPERS
    ═══════════════════════════════════════════════════════════════ */

    private HttpHeaders buildAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (currentAuthUsername != null) headers.set("X-Auth-Username", currentAuthUsername);
        if (currentAuthRoles    != null) headers.set("X-Auth-Roles",    currentAuthRoles);
        return headers;
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

    private void extractTrainingIdFromResponse() {
        try {
            if (lastResponse != null && lastResponse.getStatusCode().is2xxSuccessful()
                    && lastResponse.getBody() != null) {
                com.fasterxml.jackson.databind.ObjectMapper om = new com.fasterxml.jackson.databind.ObjectMapper();
                com.fasterxml.jackson.databind.JsonNode json = om.readTree(lastResponse.getBody());
                if (json.has("id")) lastCreatedTrainingId = json.get("id").asLong();
            }
        } catch (Exception ignored) { /* not all responses carry an id */ }
    }
}
