package org.example.cucumber.integration.runner;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.*;

/**
 * JUnit Platform Suite runner for integration-level Cucumber features under
 * {@code features/integration}.
 *
 * <p>Integration tests use an embedded ActiveMQ broker (vm://) and verify
 * that messages are actually placed on the workload queue when trainings are
 * created or deleted via the REST API.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/integration")
@ConfigurationParameter(
        key   = PLUGIN_PROPERTY_NAME,
        value = "pretty, html:target/cucumber-reports/integration/report.html, json:target/cucumber-reports/integration/report.json")
@ConfigurationParameter(
        key   = GLUE_PROPERTY_NAME,
        value = "org.example.cucumber.integration")
@ConfigurationParameter(
        key   = FILTER_TAGS_PROPERTY_NAME,
        value = "not @Ignore")
public class CucumberIntegrationRunner {
}
