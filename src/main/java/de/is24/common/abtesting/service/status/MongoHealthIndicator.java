package de.is24.common.abtesting.service.status;

import com.mongodb.CommandResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;


@Component
public class MongoHealthIndicator implements HealthIndicator {
  @Autowired
  private MongoTemplate mongoTemplate;

  @Override
  public Health health() {
    Health.Builder healthBuilder = new Health.Builder();
    try {
      CommandResult result = this.mongoTemplate.executeCommand("{ ping: 1 }");
      healthBuilder.up().withDetail("server", result.get("serverUsed"));
    } catch (Exception ex) {
      healthBuilder.down(ex);
    }
    return healthBuilder.build();
  }
}
