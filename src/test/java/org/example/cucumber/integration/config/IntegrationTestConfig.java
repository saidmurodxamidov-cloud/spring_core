package org.example.cucumber.integration.config;

import io.cucumber.spring.CucumberContextConfiguration;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import lombok.Getter;
import org.apache.activemq.command.ActiveMQObjectMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.dto.request.TrainerWorkloadRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Spring Boot test context for integration-level Cucumber scenarios.
 *
 * <p>The {@code integration-test} profile wires in an embedded ActiveMQ broker
 * using the {@code vm://} transport, ensuring real JMS message delivery without
 * needing an external broker.
 *
 * <p>The inner {@link TestWorkloadListener} bean subscribes to the same queue
 * as the real workload service so that tests can assert on published messages.
 */
@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration-test")
@Import(IntegrationTestConfig.TestWorkloadListener.class)
public class IntegrationTestConfig {

    /**
     * Test-only JMS listener that drains messages from the workload queue and
     * puts them into a {@link BlockingQueue} for assertion in step definitions.
     */
    @TestConfiguration
    @Component
    public static class TestWorkloadListener {

        @Getter
        private final BlockingQueue<CapturedMessage> receivedMessages = new LinkedBlockingQueue<>();

        private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

        @JmsListener(destination = "${app.queue.workload}")
        public void onMessage(Message message) throws JMSException {
            String idempotencyKey = message.getStringProperty("idempotencyKey");
            TrainerWorkloadRequest request = null;

            try {
                if (message instanceof TextMessage tm) {
                    String json = tm.getText();
                    if (json != null && !json.isBlank()) {
                        request = objectMapper.readValue(json, TrainerWorkloadRequest.class);
                    }
                } else if (message instanceof ActiveMQObjectMessage om) {
                    Object payload = om.getObject();
                    if (payload instanceof TrainerWorkloadRequest twr) {
                        request = twr;
                    }
                }
            } catch (Exception ignored) {
                // If parsing fails, keep request null and still capture raw message for debugging.
            }

            receivedMessages.add(new CapturedMessage(request, idempotencyKey, message));
        }

        /** Waits up to {@code timeoutSeconds} for the next message to arrive. */
        public CapturedMessage pollMessage(long timeoutSeconds) throws InterruptedException {
            return receivedMessages.poll(timeoutSeconds, TimeUnit.SECONDS);
        }

        /** Checks that no message arrives within {@code timeoutSeconds}. */
        public boolean noMessageWithin(long timeoutSeconds) throws InterruptedException {
            return receivedMessages.poll(timeoutSeconds, TimeUnit.SECONDS) == null;
        }

        public void clearMessages() {
            receivedMessages.clear();
        }
    }

    /** Immutable holder for a captured JMS message and its metadata. */
    public record CapturedMessage(
            TrainerWorkloadRequest request,
            String idempotencyKey,
            Message rawMessage
    ) {}
}
