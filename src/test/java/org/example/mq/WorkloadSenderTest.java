package org.example.mq;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import org.example.dto.request.TrainerWorkloadRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkloadSenderTest {

    @Mock
    private JmsTemplate jmsTemplate;

    @Mock
    private IdempotencyKeyService idempotencyKeyService;

    @Mock
    private JmsMessageEnricher enricher;

    @Mock
    private Message message;

    private WorkloadSender workloadSender;
    private TrainerWorkloadRequest workloadRequest;

    @BeforeEach
    void setUp() {
        workloadSender = new WorkloadSender(jmsTemplate, idempotencyKeyService, enricher);
        ReflectionTestUtils.setField(workloadSender, "workloadQueue", "workload.queue");

        workloadRequest = new TrainerWorkloadRequest();
        workloadRequest.setActionType(ActionType.ADD);
        workloadRequest.setUsername("trainer1");
    }

    @Test
    void sendWorkload_WithTracing_SetsTraceProperties() throws Exception {
        when(idempotencyKeyService.generateKey(workloadRequest)).thenReturn("key123");

        doAnswer(invocation -> {
            MessagePostProcessor processor = invocation.getArgument(2);
            processor.postProcessMessage(message);
            return null;
        }).when(jmsTemplate).convertAndSend(anyString(), any(), any(MessagePostProcessor.class));

        workloadSender.sendWorkload(workloadRequest);

        verify(message).setStringProperty("idempotencyKey", "key123");
        verify(enricher).enrich(message, "key123");
    }

    @Test
    void sendWorkload_WithDifferentIdempotencyKeys() throws Exception {
        TrainerWorkloadRequest request1 = new TrainerWorkloadRequest();
        request1.setActionType(ActionType.ADD);
        request1.setUsername("trainer1");

        TrainerWorkloadRequest request2 = new TrainerWorkloadRequest();
        request2.setActionType(ActionType.DELETE);
        request2.setUsername("trainer2");

        when(idempotencyKeyService.generateKey(request1)).thenReturn("key1");
        when(idempotencyKeyService.generateKey(request2)).thenReturn("key2");

        doAnswer(invocation -> {
            MessagePostProcessor processor = invocation.getArgument(2);
            processor.postProcessMessage(message);
            return null;
        }).when(jmsTemplate).convertAndSend(anyString(), any(), any(MessagePostProcessor.class));

        workloadSender.sendWorkload(request1);
        workloadSender.sendWorkload(request2);

        verify(message).setStringProperty("idempotencyKey", "key1");
        verify(message).setStringProperty("idempotencyKey", "key2");
    }

    @Test
    void fallback_LogsError() {
        Throwable exception = new RuntimeException("Queue unavailable");

        workloadSender.fallback(workloadRequest, exception);

        verifyNoInteractions(jmsTemplate);
    }

    @Test
    void fallback_WithDifferentExceptions() {
        Throwable[] exceptions = {
                new RuntimeException("Connection refused"),
                new JMSException("Broker down"),
                new IllegalStateException("Circuit open")
        };

        for (Throwable exception : exceptions) {
            workloadSender.fallback(workloadRequest, exception);
        }

        verifyNoInteractions(jmsTemplate);
    }

    @Test
    void sendWorkload_MessagePostProcessorReturnsMessage() throws Exception {
        when(idempotencyKeyService.generateKey(workloadRequest)).thenReturn("key123");

        doAnswer(invocation -> {
            MessagePostProcessor processor = invocation.getArgument(2);
            Message result = processor.postProcessMessage(message);
            assertNotNull(result);
            assertEquals(message, result);
            return null;
        }).when(jmsTemplate).convertAndSend(anyString(), any(), any(MessagePostProcessor.class));

        workloadSender.sendWorkload(workloadRequest);

        verify(jmsTemplate).convertAndSend(anyString(), any(), any(MessagePostProcessor.class));
    }
}