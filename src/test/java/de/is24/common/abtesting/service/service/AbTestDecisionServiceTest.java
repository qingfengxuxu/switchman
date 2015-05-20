package de.is24.common.abtesting.service.service;

import com.mongodb.BasicDBObject;
import de.is24.common.abtesting.service.repo.AbTestDecisionRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapreduce.GroupBy;
import org.springframework.data.mongodb.core.mapreduce.GroupByResults;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class AbTestDecisionServiceTest {
  public static final String TEST_NAME = "test";
  @Mock
  private AbTestDecisionRepository repository;
  @Mock
  private MongoTemplate mongoTemplate;

  private AbTestDecisionService service;
  private Map<String, Long> decisionCountForTests;
  private List<TestNameCounter> testNameCounters = Arrays.asList(createTestNameCounter(TEST_NAME, 1));

  @Before
  public void setUp() {
    service = new AbTestDecisionService(repository, mongoTemplate);
  }


  @Test
  public void returnsNumberOfDecisionsForTestConfigurations() {
    givenMongoTemplateReturnsTestNameCounters();
    whenCountDecisionsForConfigurations();
    thenDecisionCountsForTestsReturned();
  }

  @Test
  public void returnsEmptyMapWhenNoDecisionsGiven() {
    givenMongoTemplateReturnsNoTestNameCounters();
    whenCountDecisionsForConfigurations();
    thenDecisionCountsForTestsIsEmpty();
  }

  private void givenMongoTemplateReturnsNoTestNameCounters() {
    when(
      mongoTemplate.group(eq(AbTestDecisionService.DECISIONS_COLLECTION_NAME),
        any(GroupBy.class),
        eq(TestNameCounter.class))).thenReturn(
      new GroupByResults<TestNameCounter>(Collections.<TestNameCounter>emptyList(), new BasicDBObject()));
  }

  private void givenMongoTemplateReturnsTestNameCounters() {
    when(
      mongoTemplate.group(eq(AbTestDecisionService.DECISIONS_COLLECTION_NAME),
        any(GroupBy.class),
        eq(TestNameCounter.class))).thenReturn(
      new GroupByResults<TestNameCounter>(testNameCounters, new BasicDBObject()));
  }

  protected void whenCountDecisionsForConfigurations() {
    this.decisionCountForTests = service.countDecisionsForConfigurations();
  }

  private void thenDecisionCountsForTestsReturned() {
    assertThat(decisionCountForTests, hasEntry(TEST_NAME, 1L));
  }

  private void thenDecisionCountsForTestsIsEmpty() {
    assertThat(decisionCountForTests.size(), is(0));
  }


  private TestNameCounter createTestNameCounter(String testName, long count) {
    return new TestNameCounter(testName, count);
  }


}
