package org.example.messageQueue;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import java.util.Map;

@Configuration
//@EnableJms
public class ActiveMQConfig {

    // This tells JmsTemplate and @JmsListener to use JSON instead of Java serialization.
    // Producer serializes object → JSON string, consumer deserializes JSON string → object.
    // Package names no longer matter — only field names must match.
    @Bean
    public MessageConverter jacksonJmsMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");

        // 1. Create a dedicated ObjectMapper
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

        // 2. Register the module that handles Java 8 Dates (LocalDate)
        mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

        // 3. Force ISO-8601 format (2026-02-24) instead of numeric arrays
        mapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        converter.setObjectMapper(mapper);

        converter.setTypeIdMappings(Map.of(
                "TrainerWorkloadRequest", org.example.dto.request.TrainerWorkloadRequest.class
        ));
        return converter;
    }

    @Bean
    public ActiveMQConnectionFactory connectionFactory(
            @Value("${spring.activemq.broker-url}") String brokerUrl,
            @Value("${spring.activemq.user}") String user,
            @Value("${spring.activemq.password}") String password) {

        ActiveMQConnectionFactory factory =
                new ActiveMQConnectionFactory(user, password, brokerUrl);
        // no more setTrustedPackages needed — JSON doesn't use Java serialization
        return factory;
    }

    @Bean
    public JmsTemplate jmsTemplate(ActiveMQConnectionFactory connectionFactory,
                                   MessageConverter jacksonJmsMessageConverter) {
        JmsTemplate template = new JmsTemplate(connectionFactory);
        template.setMessageConverter(jacksonJmsMessageConverter);  // ← wire it in
        template.setDeliveryPersistent(true);
        template.setSessionTransacted(true);
        return template;
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(
            ActiveMQConnectionFactory connectionFactory,
            MessageConverter jacksonJmsMessageConverter) {
        DefaultJmsListenerContainerFactory factory =
                new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jacksonJmsMessageConverter);  // ← wire it in
        factory.setSessionTransacted(true);
        factory.setConcurrency("1-5");
        return factory;
    }
}