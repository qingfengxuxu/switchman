package de.is24.common.abtesting.service.reporting;

import com.netflix.hystrix.HystrixCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestOperations;


public class DecisionToHadoopExportCommand extends HystrixCommand<HttpStatus> {
  private static final Logger LOGGER = LoggerFactory.getLogger(DecisionToHadoopExportCommand.class);

  private final RestOperations restOperations;
  private final String hadoopRestUrl;

  private static final String HADOOP_API_PATH = "/V1/event/";
  private static final String EVENT_NAME = "AbTestDecision";
  private final HttpEntity<DwhOldSchoolAbDecision> wrappedEntity;

  public DecisionToHadoopExportCommand(Setter setter, RestOperations restOperations, String hadoopRestUrl,
                                       HttpEntity<DwhOldSchoolAbDecision> wrappedEntity) {
    super(setter);
    this.restOperations = restOperations;
    this.hadoopRestUrl = hadoopRestUrl;
    this.wrappedEntity = wrappedEntity;
  }

  @Override
  protected HttpStatus run() throws Exception {
    LOGGER.debug("Starting to export AB test decision to {}.", hadoopRestUrl);

    ResponseEntity<Object> exchange = restOperations.exchange(hadoopRestUrl + HADOOP_API_PATH + EVENT_NAME,
      HttpMethod.PUT,
      wrappedEntity,
      (Class) null);

    if (!exchange.getStatusCode().is2xxSuccessful()) {
      LOGGER.warn("Failed to report decision to Hadoop. Status: {}", exchange.getStatusCode());
    }

    LOGGER.debug("Finished to export decision to {}, status: {}", hadoopRestUrl, exchange.getStatusCode());
    return exchange.getStatusCode();
  }

  @Override
  protected HttpStatus getFallback() {
    LOGGER.warn("Hadoop export failed.", this.getFailedExecutionException());
    return HttpStatus.SERVICE_UNAVAILABLE;
  }
}
