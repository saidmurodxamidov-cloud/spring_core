package org.example.config;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.sql.Connection;

@Configuration
@Slf4j
@PropertySource(value = "classpath:application.yml", factory = YamlPropertySourceFactory.class)
public class DataSourceConfig {

    @Autowired
    private Environment env;

    @PostConstruct
    public void printEnv() {
        log.debug("DB_USERNAME={}", env.getProperty("DB_USERNAME"));
        log.debug("DB_PASSWORD={}", env.getProperty("DB_PASSWORD"));
    }
    @Bean
    public DataSource dataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setDriverClassName(env.getProperty("db.driver"));
        ds.setJdbcUrl(env.getProperty("db.url"));
        ds.setUsername(env.getProperty("db.username"));
        ds.setPassword(env.getProperty("db.password"));
        ds.setMaximumPoolSize(10);
        return ds;
    }
    @Bean
    public boolean testConnection(DataSource dataSource) {
        try (Connection conn = dataSource.getConnection()) {
            log.info("Connected: " + !conn.isClosed());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return true;
    }

}
