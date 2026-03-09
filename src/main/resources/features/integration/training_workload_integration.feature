Feature: Training Service to Workload Service Integration
  As a platform operator
  I want training events to be reliably delivered to the workload queue
  So that trainer workload summaries are always kept up to date

  Background:
    Given the integration database is clean
    And the following training type exists for integration:
      | name |
      | YOGA |
    And the following trainer exists for integration:
      | username  | firstName | lastName |
      | john.doe  | John      | Doe      |
    And the following trainee exists for integration:
      | username   | firstName | lastName |
      | jane.smith | Jane      | Smith    |

  # ─── POSITIVE INTEGRATION SCENARIOS ─────────────────────────────────────

  Scenario: Creating a training publishes an ADD workload message to the queue
    Given I am authenticated as "john.doe" with roles "ROLE_TRAINER"
    When I send an integration POST request to "/api/trainings" with body:
      | trainingName              | Integration Yoga |
      | traineeUsername           | jane.smith       |
      | trainerUsername           | john.doe         |
      | trainingType              | YOGA             |
      | trainingDate              | 2024-06-15       |
      | trainingDurationInMinutes | 60               |
    Then the training creation response status should be 201
    And within 5 seconds a message should arrive on the workload queue
    And the received workload message should have action "ADD"
    And the received workload message should have trainerUsername "john.doe"
    And the received workload message should have duration 60 minutes

  Scenario: Deleting an existing training publishes a DELETE workload message to the queue
    Given I am authenticated as "john.doe" with roles "ROLE_TRAINER"
    And a training "Integration Morning Yoga" exists for integration with trainer "john.doe" and trainee "jane.smith"
    When I send an integration DELETE request for the last created training
    Then the delete training response status should be 200
    And within 5 seconds a message should arrive on the workload queue
    And the received workload message should have action "DELETE"
    And the received workload message should have trainerUsername "john.doe"

  Scenario: Deleting a non-existing training does NOT publish any workload message
    Given I am authenticated as "john.doe" with roles "ROLE_TRAINER"
    When I send a DELETE request to "/api/trainings/88888" for integration
    Then the delete training response status should be 200
    And no workload message should arrive on the queue within 3 seconds

  # ─── NEGATIVE INTEGRATION SCENARIOS ─────────────────────────────────────

  Scenario: Failed training creation does not publish any workload message
    Given I am authenticated as "john.doe" with roles "ROLE_TRAINER"
    When I send an integration POST request to "/api/trainings" with body:
      | trainingName              | Ghost Training |
      | traineeUsername           | ghost.trainee  |
      | trainerUsername           | john.doe       |
      | trainingType              | YOGA           |
      | trainingDate              | 2024-06-15     |
      | trainingDurationInMinutes | 60             |
    Then the training creation response status should be 4xx or 5xx
    And no workload message should arrive on the queue within 3 seconds

  Scenario: Workload message contains correct idempotency key header
    Given I am authenticated as "john.doe" with roles "ROLE_TRAINER"
    When I send an integration POST request to "/api/trainings" with body:
      | trainingName              | Idempotency Test |
      | traineeUsername           | jane.smith       |
      | trainerUsername           | john.doe         |
      | trainingType              | YOGA             |
      | trainingDate              | 2024-07-01       |
      | trainingDurationInMinutes | 30               |
    Then the training creation response status should be 201
    And within 5 seconds a message should arrive on the workload queue
    And the received workload message should have a non-empty idempotency key header
