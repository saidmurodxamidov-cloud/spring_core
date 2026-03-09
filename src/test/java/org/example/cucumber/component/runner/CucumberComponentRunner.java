package org.example.cucumber.component.runner;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.*;


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
