package org.example.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Getter
@Component
@NoArgsConstructor
public class HibernateProperties {

    @Value("${hibernate.ddl-auto}")
    private String ddlAuto;

    @Value("${hibernate.dialect}")
    private String dialect;

    @Value("${hibernate.show-sql}")
    private String showSql;

    @Value("${hibernate.format-sql}")
    private String formatSql;

    @Value("${hibernate.jdbc.lob.non-contextual-creation}")
    private String nonContextualLob;

    @Value("${hibernate.temp.use-jdbc-metadata-defaults}")
    private String useMetadataDefaults;

    public Properties toProperties() {
        Properties props = new Properties();
        props.setProperty("hibernate.hbm2ddl.auto", ddlAuto);
        props.setProperty("hibernate.dialect", dialect);
        props.setProperty("hibernate.show_sql", showSql);
        props.setProperty("hibernate.format_sql", formatSql);
        props.setProperty("hibernate.jdbc.lob.non_contextual_creation", nonContextualLob);
        props.setProperty("hibernate.temp.use_jdbc_metadata_defaults", useMetadataDefaults);
        return props;
    }
}
