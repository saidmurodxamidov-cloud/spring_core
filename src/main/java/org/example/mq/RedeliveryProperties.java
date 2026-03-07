package org.example.mq;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Data
@ConfigurationProperties(prefix = "spring.activemq.redelivery")
@Component
public class RedeliveryProperties {
    private int maximumRedeliveries;
    private long initialRedeliveryDelay;
    private boolean useExponentialBackOff;
    private double backOffMultiplier;
}