package org.example.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


@Component
@RequiredArgsConstructor
public class ApplicationInfoContributor implements InfoContributor {

    private final Environment environment;
    private final EnvironmentInfo environmentInfo;

    @Override
    public void contribute(Info.Builder builder) {
        Map<String, Object> details = new HashMap<>();
        details.put("application", environment.getProperty("spring.application.name", "gym-management-system"));
        details.put("profiles", environment.getActiveProfiles());
        details.put("port", environment.getProperty("server.port", "8080"));
        details.put("database", maskDatabaseUrl(environment.getProperty("spring.datasource.url")));
        details.put("isLocal", environmentInfo.isLocal());
        details.put("isDev", environmentInfo.isDev());
        details.put("isStaging", environmentInfo.isStaging());
        details.put("isProduction", environmentInfo.isProduction());
        
        builder.withDetails(details);
    }

    private String maskDatabaseUrl(String url) {
        if (url == null) return "N/A";
        return url.replaceAll("://.*@", "://***:***@");
    }
}
