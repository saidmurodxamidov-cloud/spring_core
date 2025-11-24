package org.example.config;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.sql.DataSource;


@Configuration
@Slf4j
@PropertySource(value = "classpath:application.yml", factory = YamlPropertySourceFactory.class)

@RequiredArgsConstructor
public class DataSourceConfig {

    private final DataSourceProperties dataSourceProperties;

    @Bean
    public DataSource dataSource() {
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setDriverClassName(dataSourceProperties.getDbDriver());
        hikariDataSource.setJdbcUrl(dataSourceProperties.getDbDriver());
        hikariDataSource.setUsername(dataSourceProperties.getDbUsername());
        hikariDataSource.setPassword(dataSourceProperties.getDbPassword());
        hikariDataSource.setMaximumPoolSize(10);
        return hikariDataSource;

    }
}
