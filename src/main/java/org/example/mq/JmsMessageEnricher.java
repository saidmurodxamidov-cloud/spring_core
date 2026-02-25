package org.example.mq;

import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import org.slf4j.MDC;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

@Service
public class JmsMessageEnricher {

    private final Tracer tracer;

    public JmsMessageEnricher(ObjectProvider<Tracer> tracerProvider) {
        this.tracer = tracerProvider.getIfAvailable();
    }

    public void enrich(Message message, String idempotencyKey) throws JMSException {
        message.setStringProperty("idempotencyKey", idempotencyKey);

        if (tracer != null && tracer.currentSpan() != null) {
            TraceContext ctx = tracer.currentSpan().context();
            message.setStringProperty("traceId", ctx.traceId());
            message.setStringProperty("spanId",  ctx.spanId());
        } else {
            String traceId = MDC.get("traceId");
            String spanId  = MDC.get("spanId");
            if (traceId != null) message.setStringProperty("traceId", traceId);
            if (spanId  != null) message.setStringProperty("spanId",  spanId);
        }
    }
}