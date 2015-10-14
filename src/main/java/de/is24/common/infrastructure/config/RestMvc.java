package de.is24.common.infrastructure.config;

import de.is24.common.abtesting.remote.api.AbTestVariant;
import de.is24.common.abtesting.service.domain.AbTestConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.core.event.ValidatingRepositoryEventListener;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import java.net.URI;


@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true)
@Import(RepositoryRestMvcConfiguration.class)
public class RestMvc extends RepositoryRestMvcConfiguration {
  @Autowired
  private MessageSource messageSource;

  @Bean
  public Validator validator() {
    LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
    localValidatorFactoryBean.setValidationMessageSource(messageSource);
    return localValidatorFactoryBean;
  }

  @Override
  protected void configureValidatingRepositoryEventListener(ValidatingRepositoryEventListener validatingListener) {
    validatingListener.addValidator("beforeCreate", validator());
  }

  @Override
  protected void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
    config.setBaseUri(URI.create(config.getBaseUri().getPath() + "/api"));
    config.exposeIdsFor(AbTestConfiguration.class);
    config.exposeIdsFor(AbTestVariant.class);
    config.setDefaultPageSize(1000);
  }
}
