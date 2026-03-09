Feature: Authentication - Component Tests
  As a system user
  I want to authenticate via the Auth API
  So that I can obtain a token and access protected resources

  Background:
    Given the database is clean
    And a user exists with username "trainer.user", password "validPass123", role "TRAINER"

  # ─── POSITIVE SCENARIOS ───────────────────────────────────────────────────

  Scenario: Successfully login with valid credentials
    When I POST to "/api/auth/login" with credentials username "trainer.user" and password "validPass123"
    Then the response status should be 200
    And the response body should contain a non-empty "token" field

  Scenario: Successfully logout after login
    Given I am authenticated as "trainer.user" with roles "ROLE_TRAINER"
    When I POST to "/api/auth/logout" with no body
    Then the response status should be 200

  Scenario: Hello endpoint is accessible without authentication
    When I GET "/api/auth" without authentication
    Then the response status should be 200
    And the response body should be "Hello world"

  # ─── NEGATIVE SCENARIOS ───────────────────────────────────────────────────

  Scenario: Fail login with wrong password
    When I POST to "/api/auth/login" with credentials username "trainer.user" and password "wrongPassword"
    Then the response status should be 401 or 403

  Scenario: Fail login with non-existing username
    When I POST to "/api/auth/login" with credentials username "nonexistent.user" and password "anyPassword"
    Then the response status should be 401 or 403

  Scenario: Fail login with empty username
    When I POST to "/api/auth/login" with credentials username "" and password "validPass123"
    Then the response status should be 4xx or 5xx

  Scenario: Fail login with empty password
    When I POST to "/api/auth/login" with credentials username "trainer.user" and password ""
    Then the response status should be 4xx or 5xx
