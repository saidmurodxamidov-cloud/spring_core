package org.example.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final MdcFilter mdcFilter;
    
    @Value("${prometheus.security.enabled:false}")
    private boolean prometheusSecurityEnabled;
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
                            new AntPathRequestMatcher("/error"),
                            new AntPathRequestMatcher("/v3/api-docs/**"),
                            new AntPathRequestMatcher("/swagger-ui/**"),
                            new AntPathRequestMatcher("/swagger-ui.html"),
                            new AntPathRequestMatcher("/webjars/**"),
                            new AntPathRequestMatcher("/actuator/health"),
                            new AntPathRequestMatcher("/actuator/info")
                    ).permitAll();
                    
                    auth.requestMatchers("/api/auth/logout").authenticated();
                    
                    if (prometheusSecurityEnabled) {
                        auth.requestMatchers("/actuator/prometheus").hasRole("PROMETHEUS");
                    } else {
                        auth.requestMatchers("/actuator/prometheus").permitAll();
                    }
                    
                    auth.requestMatchers("/actuator/**").hasRole("ADMIN")
                        .anyRequest().authenticated();
                })
                .httpBasic(httpBasic -> {
                    if (prometheusSecurityEnabled) {
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
        
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",
            "http://localhost:8080",
            "http://localhost:4200"
        ));
        
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "X-Requested-With",
            "Accept",
            "Origin"
        ));
        
        configuration.setAllowCredentials(true);
        
        configuration.setExposedHeaders(List.of("Authorization"));
        
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }

}