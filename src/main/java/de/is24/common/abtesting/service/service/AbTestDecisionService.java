package de.is24.common.abtesting.service.service;

import com.google.common.collect.Maps;
import com.mongodb.BasicDBObject;
import de.is24.common.abtesting.service.domain.AbTestDecision;
import de.is24.common.abtesting.service.repo.AbTestDecisionRepository;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.mapreduce.GroupBy;
import org.springframework.data.mongodb.core.mapreduce.GroupByResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;


@Service
public class AbTestDecisionService {
  private final Logger LOGGER = LoggerFactory.getLogger(AbTestDecisionService.class);
  public static final String DECISIONS_COLLECTION_NAME = "abTestDecision";
  private final AbTestDecisionRepository decisionRepository;

  private final MongoTemplate mongoTemplate;

  @Autowired
  public AbTestDecisionService(AbTestDecisionRepository decisionRepository, MongoTemplate mongoTemplate) {
    this.decisionRepository = decisionRepository;
    this.mongoTemplate = mongoTemplate;
  }

  public Iterable<AbTestDecision> findByDecisionIds(List<String> decisionIds) {
    return decisionRepository.findAll(decisionIds);
  }

  public Long deleteByTestName(String testName) {
    return decisionRepository.deleteByTestName(testName);
  }

  public List<AbTestDecision> findBySsoId(String userSsoId) {
    return decisionRepository.findByUserSsoId(userSsoId);
  }

  public Map<String, DateTime> latestDecisionForConfigurations() {
    Aggregation aggregation = Aggregation.newAggregation(
      project("testName", "created"),
      group("testName").max("created").as("latestDecision"),
      project("latestDecision").and("testName").previousOperation());
    AggregationResults<LatestDecision> latestDecisions = mongoTemplate.aggregate(aggregation,
      DECISIONS_COLLECTION_NAME,
      LatestDecision.class);

    return toMap(latestDecisions);

  }

  private Map<String, DateTime> toMap(AggregationResults<LatestDecision> latestDecisions) {
    Map<String, DateTime> results = Maps.newHashMap();
    for (LatestDecision latestDecision : latestDecisions) {
      results.put(latestDecision.getTestName(), latestDecision.getLatestDecision());
    }

    return results;
  }

  public Map<String, Long> countDecisionsForConfigurations() {
    GroupByResults<TestNameCounter> testNameCounters = mongoTemplate.group(DECISIONS_COLLECTION_NAME,
      GroupBy.key("testName")
      .initialDocument(new BasicDBObject("count", 0))
      .reduceFunction("function(doc, prev) { prev.count += 1 }"),
      TestNameCounter.class);

    return toMap(testNameCounters);
  }

  private Map<String, Long> toMap(GroupByResults<TestNameCounter> testNameCounters) {
    Map<String, Long> results = Maps.newHashMap();

    for (TestNameCounter testNameCounter : testNameCounters) {
      results.put(testNameCounter.getTestName(), testNameCounter.getCount());
    }

    return results;
  }

  public List<OrphanedTest> findOrphans(Collection<String> existingConfigurations) {
    Aggregation aggregation = Aggregation.newAggregation(
      match(Criteria.where("testName").nin(existingConfigurations)),
      group("testName").max("created").as("latestCreatedDate").count().as("count"),
      project("latestCreatedDate", "count").and("testName").previousOperation());

    AggregationResults<OrphanedTest> orphanedTests = mongoTemplate.aggregate(aggregation,
      DECISIONS_COLLECTION_NAME,
      OrphanedTest.class);

    return orphanedTests.getMappedResults();
  }


  public void delete(String userSsoId, String testName) {
    AbTestDecision testDecision = decisionRepository.findByTestNameAndUserSsoId(testName, userSsoId);
    if (testDecision != null) {
      decisionRepository.delete(testDecision.getId());
    }
  }

  public void update(String userSsoId, String testName, Integer variantId) {
    AbTestDecision testDecision = decisionRepository.findByTestNameAndUserSsoId(testName, userSsoId);
    if (testDecision != null) {
      testDecision.setVariantId(variantId);
      decisionRepository.save(testDecision);
    }
  }

  public void saveBatch(Collection<AbTestDecision> testDecisions) {
    for (AbTestDecision testDecision : testDecisions) {
      AbTestDecision existingDecisionForTestAndUser = decisionRepository.findByTestNameAndUserSsoId(
        testDecision.getTestName(),
        testDecision.getUserSsoId());
      if (existingDecisionForTestAndUser != null) {
        LOGGER.debug("Deleting existing decision for test {} and SSO-ID {}.",
          existingDecisionForTestAndUser.getTestName(),
          existingDecisionForTestAndUser.getUserSsoId());
        decisionRepository.delete(existingDecisionForTestAndUser.getId());
      }
    }
    decisionRepository.save(testDecisions);
  }
}
