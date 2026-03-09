package org.example.cucumber.component.config;

import io.cucumber.spring.CucumberContextConfiguration;
import org.example.mq.WorkloadSenderDelegate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;


@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("component-test")
public class ComponentTestConfig {


    @MockBean
    public WorkloadSenderDelegate workloadSenderDelegate;
}
