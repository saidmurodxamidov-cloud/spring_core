package org.example.cucumber.component.runner;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.*;

/**
 * JUnit Platform Suite runner that discovers and executes all component-level
 * Cucumber features under {@code features/component}.
 *
 * <p>These tests boot the full Spring context but replace the JMS
 * WorkloadSenderDelegate with a Mockito mock, so no real ActiveMQ is needed.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/component")
@ConfigurationParameter(
        key   = PLUGIN_PROPERTY_NAME,
        value = "pretty, html:target/cucumber-reports/component/report.html, json:target/cucumber-reports/component/report.json")
@ConfigurationParameter(
        key   = GLUE_PROPERTY_NAME,
        value = "org.example.cucumber.component")
@ConfigurationParameter(
        key   = FILTER_TAGS_PROPERTY_NAME,
        value = "not @Ignore")
public class CucumberComponentRunner {
}
