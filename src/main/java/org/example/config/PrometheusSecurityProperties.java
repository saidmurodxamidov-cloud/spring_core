package org.example.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "prometheus.security")
public class PrometheusSecurityProperties {
    private boolean enabled = false;
    private String username = "prometheus";
    private String password = "prometheus";
}

