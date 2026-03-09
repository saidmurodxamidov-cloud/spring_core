package org.example.cucumber.component.config;

import io.cucumber.spring.CucumberContextConfiguration;
import org.example.mq.WorkloadSenderDelegate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

/**
 * Defines the Spring Boot test context for all component-level Cucumber scenarios.
 *
 * <p>The {@link WorkloadSenderDelegate} is replaced by a Mockito mock so that
 * no real ActiveMQ broker is required and outbound JMS calls can be verified
 * without side effects.
 *
 * <p>The {@code component-test} profile loads {@code application-component-test.yml}
 * which wires up an in-memory H2 database and disables Eureka.
 */
@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("component-test")
public class ComponentTestConfig {

    /**
     * Replaces the real JMS sender with a Mockito mock.
     * Step definitions inject this bean via {@code @Autowired} to verify interactions.
     */
    @MockBean
    public WorkloadSenderDelegate workloadSenderDelegate;
}
