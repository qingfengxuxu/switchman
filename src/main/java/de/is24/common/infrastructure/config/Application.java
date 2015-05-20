package de.is24.common.infrastructure.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;


@ComponentScan(
  basePackages = {
    "de.is24.common.infrastructure", "de.is24.common.abtesting.service", "de.is24.common.togglz.service"
  }
)
@EnableAutoConfiguration
@EnableConfigurationProperties
public class Application extends SpringBootServletInitializer {
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
