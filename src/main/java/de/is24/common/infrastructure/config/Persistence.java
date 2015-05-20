package de.is24.common.infrastructure.config;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import de.is24.common.infrastructure.domain.Client;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


@Configuration
@EnableMongoAuditing
@EnableMongoRepositories(
  {
    "de.is24.common.infrastructure.repo", "de.is24.common.abtesting.service.repo", "de.is24.common.togglz.service.repo"
  }
)
@Profile("default")
public class Persistence implements DisposableBean {
  @Value("${mongodb.url:your.mongodb.server}")
  private String mongoDbUrl;

  private MongoClient mongo;

  @Bean
  public MongoDbFactory mongoDbFactory() throws Exception {
    MongoClientURI uri = new MongoClientURI(mongoDbUrl);
    mongo = new MongoClient(uri);
    mongo.setReadPreference(ReadPreference.primary());
    mongo.setWriteConcern(WriteConcern.ACKNOWLEDGED);
    return new SimpleMongoDbFactory(mongo, uri.getDatabase());
  }

  @Bean
  public AuditorAware<String> auditorProvider() {
    return
      () -> {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

      if ((authentication == null) || !authentication.isAuthenticated()) {
        return null;
      }

      return ((Client) authentication.getPrincipal()).getUsername();
    };
  }

  @Override
  public void destroy() throws Exception {
    mongo.close();
  }
}
