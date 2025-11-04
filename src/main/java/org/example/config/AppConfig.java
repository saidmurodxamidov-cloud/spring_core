package org.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

@Configuration
@ComponentScan(basePackages = "org.example")
public class AppConfig {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfig() throws Exception {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();

        Yaml yaml = new Yaml();
        try (InputStream in = AppConfig.class.getClassLoader().getResourceAsStream("application.yml")) {
            if (in == null) throw new RuntimeException("YAML file not found");
            Map<String, Object> yamlMap = yaml.load(in);
            Properties props = new Properties();
            flattenMap("", yamlMap, props);
            configurer.setProperties(props);
        }

        return configurer;
    }

    private static void flattenMap(String prefix, Map<String, Object> map, Properties props) {
        map.forEach((k, v) -> {
            String key = prefix.isEmpty() ? k : prefix + "." + k;
            if (v instanceof Map) {
                flattenMap(key, (Map<String, Object>) v, props);
            } else {
                props.put(key, v.toString());
            }
        });
    }
}
