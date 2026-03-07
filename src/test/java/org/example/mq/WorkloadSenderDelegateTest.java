package org.example.mq;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import org.example.dto.request.TrainerWorkloadRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkloadSenderDelegateTest {

    @Mock
    private JmsTemplate jmsTemplate;

    @Mock
    private JmsMessageEnricher enricher;

    @Mock
    private Message message;

    private WorkloadSenderDelegate delegate;
    private TrainerWorkloadRequest workloadRequest;

    @BeforeEach
    void setUp() {
        delegate = new WorkloadSenderDelegate(jmsTemplate, enricher);
        ReflectionTestUtils.setField(delegate, "workloadQueue", "workload.queue");

        workloadRequest = new TrainerWorkloadRequest();
        workloadRequest.setActionType(ActionType.ADD);
        workloadRequest.setUsername("trainer1");
    }

    @Test
    void sendWorkload_SetsIdempotencyKeyAndEnrichesMessage() throws Exception {
        doAnswer(invocation -> {
            MessagePostProcessor processor = invocation.getArgument(2);
            processor.postProcessMessage(message);
            return null;
        }).when(jmsTemplate).convertAndSend(anyString(), any(), any(MessagePostProcessor.class));

        delegate.sendWorkload(workloadRequest, "key123");

        verify(message).setStringProperty("idempotencyKey", "key123");
        verify(enricher).enrich(message, "key123");
    }

    @Test
    void sendWorkload_MessagePostProcessorReturnsMessage() throws Exception {
        doAnswer(invocation -> {
            MessagePostProcessor processor = invocation.getArgument(2);
            Message result = processor.postProcessMessage(message);
            assertNotNull(result);
            assertEquals(message, result);
            return null;
        }).when(jmsTemplate).convertAndSend(anyString(), any(), any(MessagePostProcessor.class));

        delegate.sendWorkload(workloadRequest, "key123");

        verify(jmsTemplate).convertAndSend(anyString(), any(), any(MessagePostProcessor.class));
    }

    @Test
    void fallback_LogsErrorWithoutInteractingWithJms() {
        delegate.fallback(workloadRequest, "key123", new RuntimeException("Queue unavailable"));

        verifyNoInteractions(jmsTemplate);
    }

    @Test
    void fallback_HandlesVariousExceptionTypes() {
        Throwable[] exceptions = {
                new RuntimeException("Connection refused"),
                new JMSException("Broker down"),
                new IllegalStateException("Circuit open")
        };

        for (Throwable exception : exceptions) {
            delegate.fallback(workloadRequest, "key123", exception);
        }

        verifyNoInteractions(jmsTemplate);
    }
}