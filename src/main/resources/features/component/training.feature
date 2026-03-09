Feature: Training Management - Component Tests
  As an authorized system user
  I want to manage training sessions via the REST API
  So that trainings are properly tracked and workload events are dispatched

  Background:
    Given the database is clean
    And the following training type exists:
      | name     |
      | YOGA     |
    And the following trainer exists:
      | username  | firstName | lastName | password  |
      | john.doe  | John      | Doe      | secret123 |
    And the following trainee exists:
      | username   | firstName | lastName | password  |
      | jane.smith | Jane      | Smith    | secret456 |

  # ─── POSITIVE SCENARIOS ───────────────────────────────────────────────────

  Scenario: Successfully create a training with valid data
    Given I am authenticated as "john.doe" with roles "ROLE_TRAINER"
    When I send a POST request to "/api/trainings" with body:
      | trainingName              | Morning Yoga |
      | traineeUsername           | jane.smith   |
      | trainerUsername           | john.doe     |
      | trainingType              | YOGA         |
      | trainingDate              | 2024-06-15   |
      | trainingDurationInMinutes | 60           |
    Then the response status should be 201
    And the response body should contain field "trainingName" with value "Morning Yoga"
    And a workload ADD event should be dispatched for trainer "john.doe"

  Scenario: Successfully create multiple trainings for the same trainer and trainee
    Given I am authenticated as "john.doe" with roles "ROLE_TRAINER"
    When I send a POST request to "/api/trainings" with body:
      | trainingName              | Morning Yoga   |
      | traineeUsername           | jane.smith     |
      | trainerUsername           | john.doe       |
      | trainingType              | YOGA           |
      | trainingDate              | 2024-06-15     |
      | trainingDurationInMinutes | 60             |
    Then the response status should be 201
    When I send a POST request to "/api/trainings" with body:
      | trainingName              | Evening Yoga   |
      | traineeUsername           | jane.smith     |
      | trainerUsername           | john.doe       |
      | trainingType              | YOGA           |
      | trainingDate              | 2024-06-16     |
      | trainingDurationInMinutes | 45             |
    Then the response status should be 201

  Scenario: Successfully delete an existing training
    Given I am authenticated as "john.doe" with roles "ROLE_TRAINER"
    And a training "Morning Yoga" exists for trainer "john.doe" and trainee "jane.smith" of type "YOGA" on "2024-06-15" for 60 minutes
    When I delete the last created training
    Then the response status should be 200
    And a workload DELETE event should be dispatched for trainer "john.doe"

  Scenario: Deleting a non-existing training returns 200 silently
    Given I am authenticated as "john.doe" with roles "ROLE_TRAINER"
    When I send a DELETE request to "/api/trainings/99999"
    Then the response status should be 200
    And no workload event should be dispatched

  # ─── NEGATIVE SCENARIOS ───────────────────────────────────────────────────

  Scenario: Fail to create training when trainee does not exist
    Given I am authenticated as "john.doe" with roles "ROLE_TRAINER"
    When I send a POST request to "/api/trainings" with body:
      | trainingName              | Ghost Training |
      | traineeUsername           | ghost.user     |
      | trainerUsername           | john.doe       |
      | trainingType              | YOGA           |
      | trainingDate              | 2024-06-15     |
      | trainingDurationInMinutes | 60             |
    Then the response status should be 4xx or 5xx
    And no workload event should be dispatched

  Scenario: Fail to create training when trainer does not exist
    Given I am authenticated as "john.doe" with roles "ROLE_TRAINER"
    When I send a POST request to "/api/trainings" with body:
      | trainingName              | Ghost Training  |
      | traineeUsername           | jane.smith      |
      | trainerUsername           | ghost.trainer   |
      | trainingType              | YOGA            |
      | trainingDate              | 2024-06-15      |
      | trainingDurationInMinutes | 60              |
    Then the response status should be 4xx or 5xx
    And no workload event should be dispatched

  Scenario: Fail to create training when training type does not exist
    Given I am authenticated as "john.doe" with roles "ROLE_TRAINER"
    When I send a POST request to "/api/trainings" with body:
      | trainingName              | Pilates Session |
      | traineeUsername           | jane.smith      |
      | trainerUsername           | john.doe        |
      | trainingType              | PILATES         |
      | trainingDate              | 2024-06-15      |
      | trainingDurationInMinutes | 60              |
    Then the response status should be 4xx or 5xx
    And no workload event should be dispatched

  Scenario: Reject training creation when no authentication headers are provided
    When I send a POST request to "/api/trainings" without authentication with body:
      | trainingName              | Unauthorized Session |
      | traineeUsername           | jane.smith           |
      | trainerUsername           | john.doe             |
      | trainingType              | YOGA                 |
      | trainingDate              | 2024-06-15           |
      | trainingDurationInMinutes | 60                   |
    Then the response status should be 401 or 403
