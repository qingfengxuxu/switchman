package de.is24.common.infrastructure.config;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import java.io.IOException;
import static com.mongodb.ReadPreference.nearest;


@Configuration
@EnableMongoAuditing
@EnableMongoRepositories(
  {
    "de.is24.common.infrastructure.repo", "de.is24.common.abtesting.service.repo", "de.is24.common.togglz.service.repo"
  }
)
@Profile("test")
public class TestPersistence {
  private Mongo mongo;

  @Value("${test.mongodb.port:27017}")
  private Integer port;

  @Bean
  public MongoDbFactory mongoDbFactory() throws Exception {
    prepareTestDatabase(port);
    mongo = new MongoClient("localhost", port);
    mongo.setReadPreference(nearest());
    return new SimpleMongoDbFactory(mongo, "abm");
  }

  private void prepareTestDatabase(int port) throws IOException {
    MongodStarter starter = MongodStarter.getDefaultInstance();
    IMongodConfig mongodConfig = new MongodConfigBuilder().version(Version.Main.PRODUCTION)
      .net(new Net(port, Network.localhostIsIPv6()))
      .build();
    MongodExecutable mongodExecutable = starter.prepare(mongodConfig);
    mongodExecutable.start();
  }

}
