package org.example.messageQueue;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import org.example.dto.request.TrainerWorkloadRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
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
    private ObjectProvider<Tracer> tracerProvider;

    @Mock
    private Tracer tracer;

    @Mock
    private Span span;

    @Mock
    private TraceContext traceContext;

    @Mock
    private Message message;

    @Captor
    private ArgumentCaptor<MessagePostProcessor> messagePostProcessorCaptor;

    private WorkloadSender workloadSender;
    private TrainerWorkloadRequest workloadRequest;

    @BeforeEach
    void setUp() {
        when(tracerProvider.getIfAvailable()).thenReturn(tracer);
        workloadSender = new WorkloadSender(tracerProvider, jmsTemplate);
        ReflectionTestUtils.setField(workloadSender, "workloadQueue", "workload.queue");

        workloadRequest = new TrainerWorkloadRequest();
        workloadRequest.setActionType(ActionType.ADD);
        workloadRequest.setUsername("trainer1");
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void sendWorkload_Success() throws JMSException {
        String idempotencyKey = "key123";
        when(tracer.currentSpan()).thenReturn(span);
        when(span.context()).thenReturn(traceContext);
        when(traceContext.traceId()).thenReturn("trace123");
        when(traceContext.spanId()).thenReturn("span456");
//        when(message.setStringProperty(anyString(), anyString())).thenReturn(message);

        doNothing().when(jmsTemplate).convertAndSend(anyString(), any(), any(MessagePostProcessor.class));

        workloadSender.sendWorkload(idempotencyKey, workloadRequest);

        verify(jmsTemplate, times(1)).convertAndSend(eq("workload.queue"), eq(workloadRequest), any(MessagePostProcessor.class));
    }

    @Test
    void sendWorkload_WithTracing_SetsTraceProperties() throws Exception {
        String idempotencyKey = "key123";
        when(tracer.currentSpan()).thenReturn(span);
        when(span.context()).thenReturn(traceContext);
        when(traceContext.traceId()).thenReturn("trace123");
        when(traceContext.spanId()).thenReturn("span456");
//        when(message.setStringProperty(anyString(), anyString())).thenReturn(message);

        doAnswer(invocation -> {
            MessagePostProcessor processor = invocation.getArgument(2);
            Message processedMessage = processor.postProcessMessage(message);
            return null;
        }).when(jmsTemplate).convertAndSend(anyString(), any(), any(MessagePostProcessor.class));

        workloadSender.sendWorkload(idempotencyKey, workloadRequest);

        verify(message).setStringProperty("idempotencyKey", "key123");
        verify(message).setStringProperty("traceId", "trace123");
        verify(message).setStringProperty("spanId", "span456");
    }

    @Test
    void sendWorkload_WithoutTracer_UsesMDC() throws Exception {
        when(tracerProvider.getIfAvailable()).thenReturn(null);
        workloadSender = new WorkloadSender(tracerProvider, jmsTemplate);
        ReflectionTestUtils.setField(workloadSender, "workloadQueue", "workload.queue");

        MDC.put("traceId", "mdcTrace123");
        MDC.put("spanId", "mdcSpan456");

        String idempotencyKey = "key123";
        when(message.setStringProperty(anyString(), anyString())).thenReturn(message);

        doAnswer(invocation -> {
            MessagePostProcessor processor = invocation.getArgument(2);
            processor.postProcessMessage(message);
            return null;
        }).when(jmsTemplate).convertAndSend(anyString(), any(), any(MessagePostProcessor.class));

        workloadSender.sendWorkload(idempotencyKey, workloadRequest);

        verify(message).setStringProperty("idempotencyKey", "key123");
        verify(message).setStringProperty("traceId", "mdcTrace123");
        verify(message).setStringProperty("spanId", "mdcSpan456");
    }

    @Test
    void sendWorkload_WithoutTracerAndMDC_OnlySetsIdempotencyKey() throws Exception {
        when(tracerProvider.getIfAvailable()).thenReturn(null);
        workloadSender = new WorkloadSender(tracerProvider, jmsTemplate);
        ReflectionTestUtils.setField(workloadSender, "workloadQueue", "workload.queue");

        String idempotencyKey = "key123";
        when(message.setStringProperty(anyString(), anyString())).thenReturn(message);

        doAnswer(invocation -> {
            MessagePostProcessor processor = invocation.getArgument(2);
            processor.postProcessMessage(message);
            return null;
        }).when(jmsTemplate).convertAndSend(anyString(), any(), any(MessagePostProcessor.class));

        workloadSender.sendWorkload(idempotencyKey, workloadRequest);

        verify(message).setStringProperty("idempotencyKey", "key123");
        verify(message, never()).setStringProperty(eq("traceId"), anyString());
        verify(message, never()).setStringProperty(eq("spanId"), anyString());
    }

    @Test
    void sendWorkload_WithNullSpan_UsesMDC() throws Exception {
        when(tracer.currentSpan()).thenReturn(null);
        MDC.put("traceId", "mdcTrace");
        MDC.put("spanId", "mdcSpan");

        String idempotencyKey = "key123";
        when(message.setStringProperty(anyString(), anyString())).thenReturn(message);

        doAnswer(invocation -> {
            MessagePostProcessor processor = invocation.getArgument(2);
            processor.postProcessMessage(message);
            return null;
        }).when(jmsTemplate).convertAndSend(anyString(), any(), any(MessagePostProcessor.class));

        workloadSender.sendWorkload(idempotencyKey, workloadRequest);

        verify(message).setStringProperty("traceId", "mdcTrace");
        verify(message).setStringProperty("spanId", "mdcSpan");
    }

    @Test
    void sendWorkload_DifferentActionTypes() {
        String[] actionTypes = {"ADD", "DELETE", "UPDATE"};

        for (String actionType : actionTypes) {
            workloadRequest.setActionType(actionType);
            when(tracer.currentSpan()).thenReturn(span);
            when(span.context()).thenReturn(traceContext);
            when(traceContext.traceId()).thenReturn("trace123");
            when(traceContext.spanId()).thenReturn("span456");

            workloadSender.sendWorkload("key-" + actionType, workloadRequest);
        }

        verify(jmsTemplate, times(3)).convertAndSend(anyString(), any(), any(MessagePostProcessor.class));
    }

    @Test
    void sendWorkload_WithDifferentIdempotencyKeys() throws Exception {
        when(tracer.currentSpan()).thenReturn(span);
        when(span.context()).thenReturn(traceContext);
        when(traceContext.traceId()).thenReturn("trace123");
        when(traceContext.spanId()).thenReturn("span456");
        when(message.setStringProperty(anyString(), anyString())).thenReturn(message);

        String key1 = "key1";
        String key2 = "key2";

        doAnswer(invocation -> {
            MessagePostProcessor processor = invocation.getArgument(2);
            processor.postProcessMessage(message);
            return null;
        }).when(jmsTemplate).convertAndSend(anyString(), any(), any(MessagePostProcessor.class));

        workloadSender.sendWorkload(key1, workloadRequest);
        workloadSender.sendWorkload(key2, workloadRequest);

        verify(message).setStringProperty("idempotencyKey", "key1");
        verify(message).setStringProperty("idempotencyKey", "key2");
    }

    @Test
    void fallback_LogsError() {
        String idempotencyKey = "key123";
        Throwable exception = new RuntimeException("Queue unavailable");

        workloadSender.fallback(idempotencyKey, workloadRequest, exception);

        verifyNoInteractions(jmsTemplate);
    }

    @Test
    void fallback_WithDifferentExceptions() {
        String idempotencyKey = "key123";

        Throwable[] exceptions = {
                new RuntimeException("Connection refused"),
                new JMSException("Broker down"),
                new IllegalStateException("Circuit open")
        };

        for (Throwable exception : exceptions) {
            workloadSender.fallback(idempotencyKey, workloadRequest, exception);
        }

        verifyNoInteractions(jmsTemplate);
    }

    @Test
    void fallback_WithNullException() {
        String idempotencyKey = "key123";

        workloadSender.fallback(idempotencyKey, workloadRequest, null);

        verifyNoInteractions(jmsTemplate);
    }

    @Test
    void sendWorkload_VerifiesQueueName() {
        when(tracer.currentSpan()).thenReturn(span);
        when(span.context()).thenReturn(traceContext);
        when(traceContext.traceId()).thenReturn("trace123");
        when(traceContext.spanId()).thenReturn("span456");

        workloadSender.sendWorkload("key123", workloadRequest);

        verify(jmsTemplate).convertAndSend(eq("workload.queue"), any(), any(MessagePostProcessor.class));
    }

    @Test
    void sendWorkload_WithEmptyIdempotencyKey() throws Exception {
        String idempotencyKey = "";
        when(tracer.currentSpan()).thenReturn(span);
        when(span.context()).thenReturn(traceContext);
        when(traceContext.traceId()).thenReturn("trace123");
        when(traceContext.spanId()).thenReturn("span456");
        when(message.setStringProperty(anyString(), anyString())).thenReturn(message);

        doAnswer(invocation -> {
            MessagePostProcessor processor = invocation.getArgument(2);
            processor.postProcessMessage(message);
            return null;
        }).when(jmsTemplate).convertAndSend(anyString(), any(), any(MessagePostProcessor.class));

        workloadSender.sendWorkload(idempotencyKey, workloadRequest);

        verify(message).setStringProperty("idempotencyKey", "");
    }

    @Test
    void sendWorkload_MessagePostProcessorReturnsMessage() throws Exception {
        String idempotencyKey = "key123";
        when(tracer.currentSpan()).thenReturn(span);
        when(span.context()).thenReturn(traceContext);
        when(traceContext.traceId()).thenReturn("trace123");
        when(traceContext.spanId()).thenReturn("span456");
        when(message.setStringProperty(anyString(), anyString())).thenReturn(message);

        doAnswer(invocation -> {
            MessagePostProcessor processor = invocation.getArgument(2);
            Message result = processor.postProcessMessage(message);
            assertNotNull(result);
            assertEquals(message, result);
            return null;
        }).when(jmsTemplate).convertAndSend(anyString(), any(), any(MessagePostProcessor.class));

        workloadSender.sendWorkload(idempotencyKey, workloadRequest);

        verify(jmsTemplate).convertAndSend(anyString(), any(), any(MessagePostProcessor.class));
    }

    @Test
    void sendWorkload_WithPartialMDC_OnlyTraceId() throws Exception {
        when(tracerProvider.getIfAvailable()).thenReturn(null);
        workloadSender = new WorkloadSender(tracerProvider, jmsTemplate);
        ReflectionTestUtils.setField(workloadSender, "workloadQueue", "workload.queue");

        MDC.put("traceId", "mdcTrace");

        String idempotencyKey = "key123";
        when(message.setStringProperty(anyString(), anyString())).thenReturn(message);

        doAnswer(invocation -> {
            MessagePostProcessor processor = invocation.getArgument(2);
            processor.postProcessMessage(message);
            return null;
        }).when(jmsTemplate).convertAndSend(anyString(), any(), any(MessagePostProcessor.class));

        workloadSender.sendWorkload(idempotencyKey, workloadRequest);

        verify(message).setStringProperty("traceId", "mdcTrace");
        verify(message, never()).setStringProperty(eq("spanId"), anyString());
    }

    @Test
    void sendWorkload_WithPartialMDC_OnlySpanId() throws Exception {
        when(tracerProvider.getIfAvailable()).thenReturn(null);
        workloadSender = new WorkloadSender(tracerProvider, jmsTemplate);
        ReflectionTestUtils.setField(workloadSender, "workloadQueue", "workload.queue");

        MDC.put("spanId", "mdcSpan");

        String idempotencyKey = "key123";
        when(message.setStringProperty(anyString(), anyString())).thenReturn(message);

        doAnswer(invocation -> {
            MessagePostProcessor processor = invocation.getArgument(2);
            processor.postProcessMessage(message);
            return null;
        }).when(jmsTemplate).convertAndSend(anyString(), any(), any(MessagePostProcessor.class));

        workloadSender.sendWorkload(idempotencyKey, workloadRequest);

        verify(message).setStringProperty("spanId", "mdcSpan");
        verify(message, never()).setStringProperty(eq("traceId"), anyString());
    }

    @Test
    void constructor_WithTracerAvailable() {
        when(tracerProvider.getIfAvailable()).thenReturn(tracer);

        WorkloadSender sender = new WorkloadSender(tracerProvider, jmsTemplate);

        assertNotNull(sender);
        verify(tracerProvider, times(1)).getIfAvailable();
    }

    @Test
    void constructor_WithoutTracerAvailable() {
        when(tracerProvider.getIfAvailable()).thenReturn(null);

        WorkloadSender sender = new WorkloadSender(tracerProvider, jmsTemplate);

        assertNotNull(sender);
        verify(tracerProvider, times(1)).getIfAvailable();
    }
}