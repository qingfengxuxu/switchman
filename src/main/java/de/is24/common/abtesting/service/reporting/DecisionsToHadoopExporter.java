package de.is24.common.abtesting.service.reporting;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import de.is24.common.abtesting.service.domain.AbTestDecision;
import javafx.beans.binding.BooleanExpression;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.event.AbstractRepositoryEventListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;

import java.nio.charset.Charset;


@Component
public class DecisionsToHadoopExporter extends AbstractRepositoryEventListener<AbTestDecision> {
  public static final MediaType JSON_MEDIA_TYPE = new MediaType("application", "json", Charset.forName("UTF-8"));
  public static final HystrixCommand.Setter HADOOP_HYSTRIX_CONFIG = HystrixCommand.Setter.withGroupKey(
    HystrixCommandGroupKey.Factory.asKey("HadoopExport"));
  private static final String CREATE_OPERATION = "I";
  private static final String UPDATE_OPERATION = "U";
  private static final String DELETE_OPERATION = "D";

  @Autowired
  private RestOperations hadoopRestOperations;

  @Value("${hadoop.enabled:false}")
  public Boolean reportToHadoop;

  @Value("${hadoop.rest.url:your.hadoop.server}")
  public String hadoopRestUrl;

  @Override
  protected void onAfterCreate(AbTestDecision entity) {
    super.onAfterSave(entity);
    export(entity, CREATE_OPERATION);
  }

  @Override
  protected void onAfterSave(AbTestDecision entity) {
    super.onAfterSave(entity);
    export(entity, UPDATE_OPERATION);
  }

  @Override
  protected void onAfterDelete(AbTestDecision entity) {
    super.onBeforeDelete(entity);
    export(entity, DELETE_OPERATION);
  }

  private void export(AbTestDecision abTestDecision, String operation) {
    if (reportToHadoop) {
      new DecisionToHadoopExportCommand(
        HADOOP_HYSTRIX_CONFIG,
        hadoopRestOperations,
        hadoopRestUrl,
        wrapHttpEntity(DwhOldSchoolAbDecision.from(abTestDecision, DateTime.now().getMillis(), operation))).queue();
    }
  }

  private HttpEntity<DwhOldSchoolAbDecision> wrapHttpEntity(DwhOldSchoolAbDecision abTestDecision) {
    return new HttpEntity(abTestDecision, getHeaders());
  }

  private static HttpHeaders getHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(JSON_MEDIA_TYPE);
    return headers;
  }
}
