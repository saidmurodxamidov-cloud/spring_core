package org.example.security.config;

import lombok.RequiredArgsConstructor;
import org.example.config.PrometheusSecurityProperties;
import org.example.persistence.entity.Role;
import org.example.security.service.GymCustomersAuthenticationService;
import org.example.security.filter.JwtAuthenticationFilter;
import org.example.security.filter.MdcFilter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@EnableConfigurationProperties({CorsProperties.class, PrometheusSecurityProperties.class})
public class SecurityConfig {

    private final GymCustomersAuthenticationService authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final MdcFilter mdcFilter;
    private final PrometheusSecurityProperties prometheusSecurityProperties;
    private final CorsProperties corsProperties;
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(authenticationProvider);
        return authenticationManagerBuilder.build();
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(
                            new AntPathRequestMatcher("/api/trainers/register"),
                            new AntPathRequestMatcher("/api/trainees/register"),
                            new AntPathRequestMatcher("/api/auth/login"),
                            new AntPathRequestMatcher("/api/training-types/**"),
                            new AntPathRequestMatcher("api/trainings/**"),
                            new AntPathRequestMatcher("/error"),
                            new AntPathRequestMatcher("/v3/api-docs/**"),
                            new AntPathRequestMatcher("/swagger-ui/**"),
                            new AntPathRequestMatcher("/swagger-ui.html"),
                            new AntPathRequestMatcher("/webjars/**"),
                            new AntPathRequestMatcher("/actuator/health"),
                            new AntPathRequestMatcher("/actuator/info")
                    ).permitAll();
                    
                    auth.requestMatchers("/api/auth/logout").authenticated();
                    
                    if (prometheusSecurityProperties.isEnabled()) {
                        auth.requestMatchers("/actuator/prometheus").hasRole(Role.PROMETHEUS.name());
                    } else {
                        auth.requestMatchers("/actuator/prometheus").permitAll();
                    }
                    
                    auth.requestMatchers("/actuator/**").hasRole(Role.ADMIN.name())
                        .anyRequest().authenticated();
                })
                .httpBasic(httpBasic -> {
                    if (prometheusSecurityProperties.isEnabled()) {
                        httpBasic.realmName("Prometheus");
                    } else {
                        httpBasic.disable();
                    }
                })
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .addFilterBefore(mdcFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        configuration.setAllowedOrigins(corsProperties.getAllowedOrigins());
        
        List<String> allowedMethods = corsProperties.getAllowedMethods().stream()
                .map(method -> {
                    try {
                        return HttpMethod.valueOf(method).name();
                    } catch (IllegalArgumentException e) {
                        return method;
                    }
                })
                .toList();
        configuration.setAllowedMethods(allowedMethods);
        
        List<String> allowedHeaders = corsProperties.getAllowedHeaders().stream()
                .map(header -> {
                    if ("Authorization".equalsIgnoreCase(header)) {
                        return HttpHeaders.AUTHORIZATION;
                    } else if ("Content-Type".equalsIgnoreCase(header)) {
                        return HttpHeaders.CONTENT_TYPE;
                    } else if ("Accept".equalsIgnoreCase(header)) {
                        return HttpHeaders.ACCEPT;
                    } else if ("Origin".equalsIgnoreCase(header)) {
                        return HttpHeaders.ORIGIN;
                    }
                    return header;
                })
                .toList();
        configuration.setAllowedHeaders(allowedHeaders);
        configuration.setAllowCredentials(corsProperties.isAllowCredentials());
        List<String> exposedHeaders = corsProperties.getExposedHeaders().stream()
                .map(header -> {
                    if ("Authorization".equalsIgnoreCase(header)) {
                        return HttpHeaders.AUTHORIZATION;
                    }
                    return header;
                })
                .toList();
        configuration.setExposedHeaders(exposedHeaders);
        configuration.setMaxAge(corsProperties.getMaxAge());
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }

}