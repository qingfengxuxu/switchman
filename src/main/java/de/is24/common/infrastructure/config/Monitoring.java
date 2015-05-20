package de.is24.common.infrastructure.config;

import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;


@Configuration
@Profile("default")
public class Monitoring {
  @Autowired
  private MetricRegistry metricRegistry;

  private static final long POLL_INTERVAL_IN_SECONDS = 60;

  @Value("${graphite.host:your.graphite.server}")
  private String graphiteHost;

  @Value("${graphite.port:42}")
  private Integer graphitePort;

  @Value("${graphite.metrics.prefix:your.graphite.metrics.prefix}")
  private String metricPrefix;


  @Bean
  @ConditionalOnExpression("${graphite.enabled:false}")
  public GraphiteReporter graphiteReporter() {
    final Graphite graphite = new Graphite(new InetSocketAddress(graphiteHost, graphitePort));
    GraphiteReporter graphiteReporter = GraphiteReporter.forRegistry(metricRegistry)
      .prefixedWith(metricPrefix)
      .convertRatesTo(TimeUnit.SECONDS)
      .convertDurationsTo(TimeUnit.MILLISECONDS)
      .filter(MetricFilter.ALL)
      .build(graphite);
    graphiteReporter.start(POLL_INTERVAL_IN_SECONDS, TimeUnit.SECONDS);
    return graphiteReporter;
  }

}
