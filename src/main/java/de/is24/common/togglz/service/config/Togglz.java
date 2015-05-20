package de.is24.common.togglz.service.config;

import de.is24.common.togglz.service.RepositoryWritingFeatureManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.togglz.console.TogglzConsoleServlet;
import org.togglz.core.bootstrap.TogglzBootstrap;
import org.togglz.core.manager.FeatureManager;
import org.togglz.core.spi.FeatureManagerProvider;
import org.togglz.servlet.TogglzFilter;


@Configuration
public class Togglz {
  @Autowired
  @Bean
  public TogglzBootstrap togglzBootstrap(RepositoryWritingFeatureManager featureManager) {
    return new TogglzBootstrap() {
      @Override
      public FeatureManager createFeatureManager() {
        return featureManager;
      }
    };
  }

  @Bean
  public ServletRegistrationBean togglzConsoleServletRegistration() {
    return new ServletRegistrationBean(new TogglzConsoleServlet(), "/admin/togglz-console/*");
  }

  @Bean
  public TogglzFilter togglzFilter() {
    return new TogglzFilter();
  }

}
