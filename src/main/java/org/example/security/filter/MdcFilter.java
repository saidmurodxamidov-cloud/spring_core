package org.example.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import io.micrometer.tracing.Tracer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class MdcFilter extends OncePerRequestFilter {

    private final Tracer tracer;

    public MdcFilter(ObjectProvider<Tracer> tracerProvider) {
        this.tracer = tracerProvider.getIfAvailable();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {
            MDC.put("requestId", UUID.randomUUID().toString());

            if (tracer != null && tracer.currentSpan() != null) {
                var span = tracer.currentSpan();
                if (span.context() != null) {
                    MDC.put("traceId", span.context().traceId());
                    MDC.put("spanId", span.context().spanId());
                }
            } else {
                // Fallback: try to read common tracing headers from incoming request
                String hdrTrace = request.getHeader("X-B3-TraceId");
                String hdrSpan = request.getHeader("X-B3-SpanId");
                if (hdrTrace != null) {
                    MDC.put("traceId", hdrTrace);
                }
                if (hdrSpan != null) {
                    MDC.put("spanId", hdrSpan);
                }
                // W3C Trace Context: traceparent: "00-<trace-id>-<span-id>-01"
                if ((hdrTrace == null || hdrSpan == null) && request.getHeader("traceparent") != null) {
                    String tp = request.getHeader("traceparent");
                    String[] parts = tp.split("-");
                    if (parts.length >= 3) {
                        String w3cTrace = parts[1];
                        String w3cSpan = parts[2];
                        if (hdrTrace == null) MDC.put("traceId", w3cTrace);
                        if (hdrSpan == null) MDC.put("spanId", w3cSpan);
                    }
                }
            }

            filterChain.doFilter(request, response);
        } finally {
            MDC.remove("requestId");
            MDC.remove("username");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
    }
}

